<?php

//================================================================================
//
// CSC2022 Team Project - Team 19 - Lloyds App Backend
//
// api.php - public API calls for mobile apps and other external clients
//
//================================================================================

// To use the API, a client must:
//
// 1). Always present a valid API key header, eg. 'API-Key: << API KEY >>' (see config.php)
// 2). Authenticate by POSTing username/password to /api/authenticate to obtain a device token.
//     This should be stored on the client.
// 3). Present a 'Device-Token' header with the token along with the 'API-Key' header to use further API calls.
//
//curl -X GET -H "API-Key: << API KEY >>" -H "Device-Token: 791609afff763d870c7c96dd1e9701c230c279ff6c7c032ee1c210ef6e0d718c"

// TODO: Retrieve bank accounts/balances etc for a user
// TODO: Retrieve game state for a user
// TODO: Store/update game state for a user
// TODO: API call for updating Google Cloud Messaging Key
// TODO: Complete TODO list ;)

// Log file tag
define('TAG', 'API: ');

//================================================================================
// API: Get bank accounts
//================================================================================
$this->respond('GET', '/accountdetails', function ($request, $response, $service) {
    error_log(TAG . 'Account details request from ' . $_SERVER['REMOTE_ADDR']);

    try {
        $user = checkAPIAuthentication($response);

        $accountBeans = $user->xownAccountList;
        $accounts = array();
        foreach ($accountBeans as $accountBean) {
            $accounts[] = accountToArray($accountBean);
        }

        sendJSONResponse($response, $accounts, API_SUCCESS);

    } catch (Exception $e) {
        sendInternalError($response, $e->getMessage());
    }
});

//================================================================================
// API: Transfer money
//================================================================================
$this->respond('POST', '/transfer', function ($request, $response, $service) {
    error_log(TAG . 'Transfer money request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);
    } catch (Exception $e) {
        sendInternalError($response, $e->getMessage());
    }
});


//================================================================================
// API: Authenticate (get device token with valid (username/password)
//      Takes JSON object with username & password fields - returns device token
//================================================================================
$this->respond('POST', '/authenticate', function ($request, $response, $service) {
    error_log(TAG . 'Login request from ' . $_SERVER['REMOTE_ADDR']);

    // Make sure a username and password have been supplied
    $loginData = json_decode(file_get_contents('php://input'), true);

    if (!$loginData || !isset($loginData['username']) || !isset($loginData['password'])) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Username/password not supplied');
        sendBadRequest($response, 'Your device did not supply a username and/or password.');
    }

    // Attempt login
    $user = R::findOne( 'user', 'email = ?', array($loginData['username']));
    if (is_null($user) || !password_verify($loginData['password'], $user->password)) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Incorrect username or password.');
        sendAccessDenied($response, 'Incorrect username or password.');
    } else {
        // Login successful; create and store device token information
        $deviceToken = R::dispense('devicetoken');
        $deviceToken->token = generateDeviceTokenHash($loginData);
        updateDeviceToken($deviceToken);

        // Associate token with user
        $user->xownDevicetokenList[] = $deviceToken;
        R::store($user);

        error_log(TAG . 'Access granted to ' . $_SERVER['REMOTE_ADDR'] . ': \'' . $loginData['username'] . '\' got device token ' . $deviceToken->token);
        $response->body(json_encode(array('deviceToken' => $deviceToken->token)));
        $response->send();
    }
});

function checkAPIKey() {
    global $apiKeys;

    if (!isset($_SERVER['HTTP_API_KEY'])) {
        error_log(TAG . 'No API key supplied by ' . $_SERVER['REMOTE_ADDR']);
        return false;
    }

    $clientName = array_search($_SERVER['HTTP_API_KEY'], $apiKeys);

    // See if the API key exists in the collection
    if ($clientName) {
        error_log(TAG . 'Valid API key (' . $clientName . ') supplied by ' . $_SERVER['REMOTE_ADDR']);
        return true;
    }

    // Access denied
    error_log(TAG . 'Invalid API key supplied by ' . $_SERVER['REMOTE_ADDR']);
    return false;
}

function checkDeviceToken() {
    // Expire old tokens
    $deviceTokens = R::findAll('deviceToken');
    foreach ($deviceTokens as $token) {
        if ($token->lastActivity < time() - DEVICE_TOKEN_EXPIRY_TIME) {
            R::trash($token);
        }
    }

    // Check to see if a token was supplied
    if (!isset($_SERVER['HTTP_DEVICE_TOKEN'])) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Device token was invalid');
        return null;
    }

    // Attempt to find the token
    $deviceToken = R::findOne('devicetoken', 'token = ?', array($_SERVER['HTTP_DEVICE_TOKEN']));
    if (is_null($deviceToken)) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Device token was invalid');
        return null;
    }

    // Try to find associated user
    $user = R::findOne('user', 'id = ?', array($deviceToken->user_id));

    // If user associated with the token no longer exists, destroy the token
    if (is_null($user)) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': User no longer exists');
        R::trash($deviceToken);
        return null;
    }

    // Update access time, and IP address
    updateDeviceToken($deviceToken);

    // Return the associated user
    return $user;
}

function checkAPIAuthentication($response) {
    error_log(TAG . 'Token authentication request from ' . $_SERVER['REMOTE_ADDR']);

    if (!checkAPIKey()) {
        sendAccessDenied($response, 'The API key supplied by your device was invalid.<br><br>Please update your app, or contact support.');
    }

    $user = checkDeviceToken();
    if (is_null($user)) {
        sendAccessDenied($response, 'Your device did not supply a valid authentication token.');
    }

    error_log(TAG . 'Successful token authentication from ' . $_SERVER['REMOTE_ADDR']);
    return $user;
}

function generateDeviceTokenHash($loginData) {
    return hash('sha256', $_SERVER['HTTP_USER_AGENT'] . $loginData['password'] . time());
}

function updateDeviceToken($deviceToken) {
    global $apiKeys;
    //$deviceToken->userAgentAndUDIDHash = generateUserAgentAndUDIDHash();
    $deviceToken->lastActivity = time();
    $deviceToken->lastIPAddress = $_SERVER['REMOTE_ADDR'];
    R::store($deviceToken);
}

function sendJSONResponse($response, $data, $statusCode) {
    $body = array_merge(
        $data,
        array('status' => $statusCode)
    );

    $response->body(json_encode($body));
    $response->send();
    exit;
}

function sendBadRequest($response, $reason) {
    $response->code(400);
    $response->body($reason);
    $response->send();
    exit;
}

function sendAccessDenied($response, $reason) {
    // We don't use real HTTP authentication here, but the 'WWW-Authenticate' response *must* be
    // sent when using code 401. Some versions of Android will throw an exception if this header
    // is missing or malformed. For example, 4.1.1 throws an exception if realm="xyz" is missing
    // double quotes, but other versions are fine with this. Be careful.
    $response->code(401);
    $response->header('WWW-Authenticate', 'None realm="Student Wellbeing"');
    $response->body($reason);
    $response->send();
    exit;
}

function sendInternalError($response, $reason) {
    $response->code(500);
    $response->body('Error 500: Internal server error.<br><br>' . $reason);
    $response->send();
    exit;
}

function accountToArray($accountBean) {
    return array(
        'id' => $accountBean->id,
        'type' => $accountBean->type,
        'nickname' => $accountBean->nickname,
        'accountNumber' => $accountBean->accountNumber,
        'sortCode' =>  $accountBean->sortCode,
        'interest' => $accountBean->interest,
        'overdraft' => $accountBean->overdraft,
        'userId' => $accountBean->userId,
        'balance' => $accountBean->balance
    );
}

// Send a push notification to one or more registered devices using Google Cloud Messaging
function sendGoogleCloudMessage($message, $ids) {
    // URL to GCM API
    $url = 'https://android.googleapis.com/gcm/send';

    // POST variables
    $post = array('registration_ids'  => $ids,
        'data' => array('message' => $message));

    // HTTP request headers
    $headers = array( 'Authorization: key=' . GOOGLE_SERVER_API_KEY, 'Content-Type: application/json' );

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

    // Get any curl errors
    $error = curl_errno($ch);

    // Close curl handle
    curl_close($ch);

    // If there was an error, return it, otherwise return Google response
    return $error ? $error : $result;
}