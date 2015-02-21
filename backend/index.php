<?php

//================================================================================
//
// CSC2022 Team Project - Team 19 - Lloyds App Backend
//
// index.php - main catch-all/routing class
//
//================================================================================

// TODO: User logged-in area with account overview and device pairing status

//================================================================================
// Initialisation
//================================================================================

// Begin a session
if(session_id() === '') {
    session_start();
}

// Enable verbose debug output
ini_set('display_errors', 'On');
error_reporting(E_ALL | E_STRICT);

// Include libraries
require_once __DIR__.'/config.php';
require_once __DIR__.'/vendor/autoload.php';
require_once __DIR__.'/rb.php';

// Init RedBean
R::setup('mysql:host=' . DATABASE_HOST . '; dbname=' . DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD, REDBEAN_FREEZE_ENABLED);
if (!R::testConnection()) {
    die('Error: Couldn\'t connect to database. Please check configuration.');
}

// Enable debug mode
R::debug(REDBEAN_DEBUG_ENABLED);

// Update request when app is installed in a subdirectory
$base = dirname($_SERVER['PHP_SELF']);
$trimmedBase = ltrim($base, '/');

// If we're not at the root of the server (Windows and Un*x), alter request
if ($trimmedBase !== '\\' && $trimmedBase !== '') {
    $_SERVER['REQUEST_URI'] = substr($_SERVER['REQUEST_URI'], strlen($base));
}

// Init Klein router
$klein = new Klein\Klein();

// Init Twig template engine
$loader = new Twig_Loader_Filesystem('templates');
$twig = new Twig_Environment($loader);

// Add baseURL global
$twig->addGlobal('baseURL', BASE_URL);

// Add userID-to-name function
$twig->addFunction(new Twig_SimpleFunction('userIdToName', function ($userId) {
    $user = R::findOne('user', 'id = ?', array($userId));
    return is_null($user) ? null : $user->firstName . ' ' . $user->surname;
}));

// Init admin account if one doesn't exist
$admin = R::findOne('user', 'id = ?', array(1));
if (is_null($admin)) {
    $registrationdata = array(
        'firstName' => 'Administrator',
        'surname' => '',
        'email' => 'admin@lloyds.com',
        'password' => 'admin'
    );

    createUser($registrationdata, true);

    die('An administrator account has been created.');
}

//================================================================================
// Separate routes for admin and API namespaces
//================================================================================
foreach(array('admin', 'api') as $controller) {
    // Include all routes defined in a file under a given namespace
    $klein->with("/$controller", "$controller.php");
}

//================================================================================
// Public Action: Login
//================================================================================
$klein->respond('POST', '/login', function ($request, $response, $service) {
    // Login details from form
    $username = strtolower(trim($_POST['username']));
    $password = $_POST['password'];

    $errorMessages = array();

    // Store login attempt time
    $_SESSION['lastLoginAttemptTime'] = time();

    // Validation
    if (empty($username)) {
        $errorMessages[] = 'The username was empty.';
    }

    if (empty($password)) {
        $errorMessages[] = 'The password was empty.';
    }

    if (count($errorMessages) > 0) {
        displayLoginError($errorMessages, $username, $password);
        logActivity('Login attempt failed: validation error.');
        exit;
    }

    // Attempt login
    $user = R::findOne('user', 'email = ?', array($username));
    if (is_null($user) || !password_verify($password, $user->password)) {
        $errorMessages[] = 'The username or password was incorrect.';
        displayLoginError($errorMessages, $username, $password);
        logActivity('Login attempt failed: incorrect username and/or password.');
        exit;
    } else {
        // Login successful; store session information
        refreshSessionData($user);

        // Redirect to site base URL
        $response->header('Location', $_SERVER['HTTP_REFERER']);
        $response->send();
        logActivity($user->email . ' logged in successfully.');
    }
});

//================================================================================
// Public Action: Register account
//================================================================================
$klein->respond('POST', '/register', function ($request, $response, $service) use ($twig) {
    $registrationData = formatUserData($_POST);
    $errorMessages = validateRegistrationData($registrationData, true);

    // Check if user already exists
    if (emailAddressExists($registrationData['email'])) {
        $errorMessages[] = 'An account already exists for the email address \'' . $registrationData['email'] . '\'.';
        logActivity('Account creation failed: account already exists for the email address \'' . $registrationData['email'] . '\'.');
    }

    // Display any form errors and re-populate registration form
    if (count($errorMessages) > 0) {
        displayPage('register.twig', array(
                'errorMessages' => $errorMessages,

                // Pass the bad data back to the form so the user can amend it
                'registrationData' => array(
                    'firstName' => $registrationData['firstName'],
                    'surname' => $registrationData['surname'],
                    'email' => $registrationData['email'],
                    'password' => $registrationData['password'],
                    'passwordVerify' => $registrationData['passwordVerify'],
                )
            )
        );
        logActivity('Account creation failed: validation errors.');
    }

    // We're good, present user with login form and success notice
    else {
        try {
            // Create the user
            createUser($registrationData, false);

            // Pass success message to login form
            $service->flash('Your account has been created. You may now log in.', FLASH_SUCCESS);
            $response->header('Location', BASE_URL . 'login');
            $response->send();

            logActivity('Account creation succeeded (' . $registrationData['email'] . ').');
        } catch (Exception $e) {
            displayError($e->getMessage());
            logActivity('Account creation failed because an exception was thrown: ' . $e->getMessage());
        }
    }
});

//================================================================================
// Public: Login page
//================================================================================
$klein->respond('GET', '/login', function ($request, $response, $service) {
    if (isset($_SESSION['userId'])) {
        $response->header('Location', BASE_URL);
        $response->send();
    } else {
        // Display login form
        displayPage('login.twig', null);
    }
});

//================================================================================
// Public: Registration page
//================================================================================
$klein->respond('GET', '/register', function () use ($twig) {
    // From password-compat 'version-test.php'
    $hash = '$2y$04$usesomesillystringfore7hnbRJHxXVLeakoG8K30oukPsA.ztMG';
    $test = crypt("password", $hash);
    $pass = $test == $hash;

    if (!$pass) {
        displayError('This version of PHP doesn\'t support password_hash() or the password-compat library. Please upgrade to PHP v5.3.7+.');
    } else {
        displayPage('register.twig', null);
    }
});

//================================================================================
// Public: Error pages
//================================================================================
$klein->onHttpError(function ($code, $router) use ($twig) {
    if ($code == 404) {
        displayError('Error 404: Page ' . $router->request()->uri() . ' does not exist.');
        logActivity('Tried to visit nonexistant URL \'' . $router->request()->uri() . '\'.');
    } else {
        displayError('Internal error. Code: ' . $code);
    }
    $router->response()->code($code);
    $router->response()->send();
});

//================================================================================
// Private Action: Logout
//================================================================================
$klein->respond('GET', '/logout', function ($request, $response, $service) {
    checkAuthentication();
    logActivity($_SESSION['email'] . ' logged out.');
    destroySession();

    $response->header('Location', BASE_URL);
    $response->send();
});

//================================================================================
// Public: Default page
//================================================================================
$klein->respond('GET', '/', function () use ($twig) {
    if (userIsLoggedIn()) {
        displayPage('logged_in_index.twig', null);
    } else {
        displayPage('index.twig', null);
    }
});

// URL routing handlers are now installed; try to dispatch the request
$klein->dispatch();

//================================================================================
// Utility functions
//================================================================================
function userIsLoggedIn() {
    $user = null;
    if (isset($_SESSION['userId'])) {
        $user = R::findOne('user', 'id = ?', array($_SESSION['userId']));
    }

    if (!is_null($user)) {
        // Update session data
        refreshSessionData($user);
        return true;
    }

    return false;
}

function checkAuthentication() {
    // If session has expired, destroy it
    if (isset($_SESSION['lastActivity']) && $_SESSION['lastActivity'] < time() - WEB_SESSION_EXPIRY_TIME) {
        logActivity('Session expired, user needs to log in again.');
        destroySession();
    }

    // If not logged in with a valid user ID or session has expired, redirect to the login form
    if (!userIsLoggedIn()) {
        destroySession();
        header('Location: ' . BASE_URL . 'login');
        exit;
    }
}

// Copies user data from database into PHP session for processing
function refreshSessionData($user) {
    $_SESSION['userId'] = (int) $user->id;
    $_SESSION['firstName'] = $user->firstName;
    $_SESSION['surname'] = $user->surname;
    $_SESSION['email'] = $user->email;
    $_SESSION['accessLevel'] = (int) $user->accessLevel;

    // Update activity timestamp
    $_SESSION['lastActivity'] = time();
}

function destroySession() {
    // Clear $_SESSION variables
    $_SESSION = array();

    // Delete server-side session and generate a new ID
    session_destroy();
    session_regenerate_id(true);
}

function displayLoginError($errorMessages, $username, $password) {
    displayPage('login.twig', array('username' => $username, 'password' => $password, 'errorMessages' => $errorMessages));
}

function displayError($error) {
    displayPage('error.twig', array('errorMessage' => $error));
}

function formatUserData($postData) {
    return array(
        'firstName' => trim($postData['firstName']),
        'surname' => trim($postData['surname']),
        'email' => strtolower(trim($postData['email'])),
        'password' => $postData['password'],
        'passwordVerify' => $postData['passwordVerify']
    );
}

function validateRegistrationData($formattedRegistrationData, $checkPasswords) {
    $errorMessages = array();

    // Validate email address
    if (!filter_var($formattedRegistrationData['email'], FILTER_VALIDATE_EMAIL)) {
        $errorMessages[] = 'The email address is invalid. Please check the format of the email address.';
    }

    // Validate first name
    if (empty($formattedRegistrationData['firstName'])) {
        $errorMessages[] = 'Please enter a first name.';
    }

    // Validate surname
    if (empty($formattedRegistrationData['surname'])) {
        $errorMessages[] = 'Please enter a surname.';
    }

    if ($checkPasswords === true) {
        // Validate password
        if (strlen($formattedRegistrationData['password']) < MINIMUM_PASSWORD_LENGTH) {
            $errorMessages[] = 'Please ensure the password contains a minimum of ' . MINIMUM_PASSWORD_LENGTH . ' characters.';
        }

        if ($formattedRegistrationData['password'] !== $formattedRegistrationData['passwordVerify']) {
            $errorMessages[] = 'The passwords you entered do not match.';
        }
    }

    return $errorMessages;
}

// Creates a user
function createUser($registrationData, $admin) {
    // Create RedBean object
    $user = R::dispense('user');

    // Assign fields
    $user->email = $registrationData['email'];
    $user->firstName = $registrationData['firstName'];
    $user->surname = $registrationData['surname'];
    $user->accessLevel = $admin ? ACCESS_LEVEL_ADMINISTRATOR : ACCESS_LEVEL_USER;
    $user->password = password_hash($registrationData['password'], PASSWORD_DEFAULT);
    $user->deviceId = null;

    // Save to database
    R::store($user);
}

// Creates a bank account
function createAccount($user, $accountData) {
    // Create RedBean object
    $account = R::dispense('account');

    // Assign fields
    $account->type = $accountData['type'];
    $account->accountNumber = ACCOUNT_TYPE_CURRENT; /* $accountData['accountNumber']; */
    $account->sortCode = $accountData['sortCode'];
    $account->nickName = $accountData['nickName'];
    $account->balance = $accountData['balance'];
    $account->overDraft = 200.0; /* $accountData['overDraft']; */
    $account->interestRate = 0.0075; /* $accountData['interestRate']; */

    // Associate account with user
    $user->xownAccountList[] = $account;

    // Save to database
    R::store($account);
}

// Creates a transaction
function createTransaction($fromAccount, $toAccount, $amount, $reference, $tag) {
    // Create RedBean object
    $transaction = R::dispense('transaction');

    // Assign fields
    $transaction->fromAccount = $fromAccount;
    $transaction->toAccount = $toAccount;
    $transaction->amount = $amount;
    $transaction->reference = $reference;
    $transaction->tag = $tag;

    // Save to database
    R::store($transaction);
}

function displayPage($template, $extraVars) {
    global $twig;
    global $klein;

    $twigVars = array();

    // If there's a user logged in, pass their details to Twig
    if (isset($_SESSION['userId'])) {
        $sessionVars = array(
            'userId' => $_SESSION['userId'],
            'firstName' => $_SESSION['firstName'],
            'surname' => $_SESSION['surname'],
            'email' => $_SESSION['email'],
            'accessLevel' => $_SESSION['accessLevel']
        );

        $twigVars['loggedInUser'] = $sessionVars;
    }

    // Pass any Klein flash messages to Twig
    $twigVars['errorMessages'] = $klein->service()->flashes(FLASH_ERROR);
    $twigVars['successMessages'] = $klein->service()->flashes(FLASH_SUCCESS);
    $twigVars['infoMessages'] = $klein->service()->flashes(FLASH_INFO);

    // Pass any extra variables to Twig
    if (!is_null($extraVars)) {
        $twigVars = array_merge($twigVars, $extraVars);
    }

    // Render the template
    echo $twig->render($template, $twigVars);
}

function userToArray($user) {
    return array(
        'id' => $user->id,
        'email' => $user->email,
        'firstName' => $user->firstName,
        'surname' => $user->surname,
        'accessLevel' => $user->accessLevel,
    );
}

function logActivity($eventDescription) {
    try {
        $event = R::dispense('logevent');
        $event->userId = isset($_SESSION['userId']) ? $_SESSION['userId'] : null;
        $event->date = time();
        $event->event = $eventDescription;
        $event->ipAddress = $_SERVER['REMOTE_ADDR'];
        R::store($event);
    } catch (Exception $e) {
        error_log('Couldn\'t create activity log event: ' . $e->getMessage());
    }
}

function checkAccessLevel($accessLevel) {
    global $klein;

    // Do regular auth check
    checkAuthentication();

    // Check for correct access level
    if ($_SESSION['accessLevel'] < $accessLevel) {
        displayError('Sorry, you don\'t have sufficient access rights to view this page.');
        logActivity($_SESSION['email'] . ' tried to visit a restricted area (' . $klein->request()->uri() . ').');
        exit;
    }
}

function emailAddressExists($email) {
    return R::findOne('user', 'email LIKE "' . $email . '"') !== null;
}

// Send a push notification to one or more registered devices using Google Cloud Messaging
function sendGoogleCloudMessage($message, $ids) {
    // URL to GCM API
    $url = 'https://android.googleapis.com/gcm/send';

    // POST variables
    $post = array( 'registration_ids'  => $ids,
                   'data'              => $message );

    // HTTP request headers
    $headers = array( 'Authorization: key=' . GOOGLE_SERVER_API_KEY,
                      'Content-Type: application/json' );

    // Initialize curl
    $ch = curl_init();

    // Set URL to GCM API URL
    curl_setopt($ch, CURLOPT_URL, $url);

    // Set request method to POST
    curl_setopt($ch, CURLOPT_POST, true);

    // Set custom headers
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

    // Get the response back as string instead of printing it
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

    // Set post data as JSON
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($post));

    // Send the push!
    $result = curl_exec($ch);

    // If there was an error, display it
    if (curl_errno($ch)) {
        echo 'GCM error: ' . curl_error($ch);
    }

    // Close curl handle
    curl_close( $ch );

    // Debug GCM response
    echo $result;
}