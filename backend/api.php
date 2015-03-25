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
// All API calls return a JSON-encoded response with a 'status' field.
// If the request was successful, 'status' will contain 'success'.
// Otherwise 'status' will contain 'error', and another field, 'errorMessage' will contain the reason.

// TODO: Secret word auth
// TODO: Retrieve game state for a user
// TODO: Store/update game state for a user

// Log file tag
define('TAG', 'API: ');

//================================================================================
// API: Get bank accounts
//================================================================================
$this->respond('GET', '/accountdetails', function ($request, $response, $service) {
    error_log(TAG . 'Account details request from ' . $_SERVER['REMOTE_ADDR']);

    try {
        $user = checkAPIAuthentication($response);
        sendJSONResponse($response, array('accounts' => R::exportAll($user->xownAccountList)));
    } catch (Exception $e) {
        sendJSONError($response, $e->getMessage());
    }
});

//================================================================================
// API: Get transactions
//================================================================================
$this->respond('GET', '/transactions', function ($request, $response, $service) {
    error_log(TAG . 'Transactions request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);

        // Make sure the account ID is valid and the user owns the account we're interested in
        $account = isset($_GET['accId']) ? R::load('account', $_GET['accId']) : null;
        if (is_null($account) || $account->userId !== $user->id) {
            sendJSONError($response, 'The account ID of the account to show transactions to/from was invalid.');
        }

        $transactions = null;

        // If a time period is specified, use it
        if (isset($_GET['periodFrom']) && isset($_GET['periodTo'])) {
            $transactions = R::find('transaction', '(from_account_id = ? OR to_account_id = ?) AND time >= ? AND time <= ?',
                array(
                    $_GET['accId'],
                    $_GET['accId'],
                    $_GET['periodFrom'],
                    $_GET['periodTo']
                )
            );
        }

        // Otherwise just return all of them
        else {
            $transactions = R::find('transaction', 'from_account_id = ? OR to_account_id = ?',
                array(
                    $_GET['accId'],
                    $_GET['accId']
                )
            );
        }

        sendJSONResponse($response, array('transactions' => R::exportAll($transactions)));
    } catch (Exception $e) {
        sendJSONError($response, $e->getMessage());
    }
});

//================================================================================
// API: Transfer money
//================================================================================
$this->respond('POST', '/transfer', function ($request, $response, $service) {
    error_log(TAG . 'Transfer money request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);
        $jsonRequest = json_decode($request->body(), true);

        // Validation
        if (is_null($jsonRequest)) {
            sendJSONError($response, 'Malformed request.');
        }

        if (!isset($jsonRequest['fromAccId'])) {
            sendJSONError($response, 'Your device did not send the account ID of the account to transfer from.');
        }

        if (!isset($jsonRequest['toAccNo'])) {
            sendJSONError($response, 'Your device did not send the account number of the account to transfer to.');
        }

        if (!isset($jsonRequest['toSortCode'])) {
            sendJSONError($response, 'Your device did not send the sort code of the account to transfer to.');
        }

        if (!isset($jsonRequest['amount'])) {
            sendJSONError($response, 'Your device did not send the amount of money to transfer.');
        }

        // Verification
        $fromAccount = R::load('account', $jsonRequest['fromAccId']);
        if (is_null($fromAccount) || $fromAccount->userId !== $user->id) {
            sendJSONError($response, 'The ID of the account to transfer from was invalid.');
        }

        $toAccount = R::findOne('account', 'sort_code = ? AND account_number = ?', array($jsonRequest['toSortCode'], $jsonRequest['toAccNo']));
        if (is_null($toAccount)) {
            sendJSONError($response, 'The account or sort code of the account to transfer to was invalid.');
        }

        $amount = intval($jsonRequest['amount']);
        if ($amount < 1) {
            sendJSONError($response, 'The amount to transfer was invalid.');
        }

        if (($fromAccount->balance + $fromAccount->overdraft) < $amount) {
            sendJSONError($response, 'There are insufficient funds available to complete this transaction.');
        }

        $reference = isset($jsonRequest['reference']) ? $jsonRequest['reference'] : "";
        $tag = isset($jsonRequest['tag']) ? $jsonRequest['tag'] : TAG_UNTAGGED;

        // Good to go...
        $fromAccount->balance -= $amount;
        $toAccount->balance += $amount;
        createTransaction($fromAccount, $toAccount, $amount, $reference, $tag);

        // Save all beans
        R::store($fromAccount);
        R::store($toAccount);

        sendJSONResponse($response, array());
    } catch (Exception $e) {
        sendJSONError($response, $e->getMessage());
    }
});

//================================================================================
// API: Update Google Cloud Messaging ID
//      Input JSON fields: 'gcmId'
//================================================================================
$this->respond('POST', '/updategcmid', function ($request, $response, $service) {
    error_log(TAG . 'GCM ID update request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);
        $jsonRequest = json_decode($request->body(), true);

        if (is_null($jsonRequest) || !isset($jsonRequest['gcmId'])) {
            sendJSONError($response, 'Your device did not supply the new GCM ID.');
        }

        // Update the GCM ID
        $user->gcmId = $jsonRequest['gcmId'];
        R::store($user);

        // Just send success status by passing an empty array
        sendJSONResponse($response, array());
    } catch (Exception $e) {
        sendJSONError($response, $e->getMessage());
    }
});

//===============================================================================================================
// API: Update User Settings
//      Input JSON fields: 'email_notifications', 'push_notifications' value of 1 for true, value of 0 for false
//===============================================================================================================
$this->respond('POST', '/updatesettings', function ($request, $response, $service) {
    error_log(TAG . 'Settings update request from ' . $_SERVER['REMOTE_ADDR']);
    try {
        $user = checkAPIAuthentication($response);
        $jsonRequest = json_decode($request->body(), true);

        if (is_null($jsonRequest) || !isset($jsonRequest['email_notifications']) || !isset($jsonRequest['push_notifications'])) {
            sendJSONError($response, 'Your device did not supply the email and push settings.');
        }

        // Update the settings
        $user->email_notifications = $jsonRequest['email_notifications'];
        $user->push_notifications = $jsonRequest['push_notifications'];
        R::store($user);

        // Just send success status by passing an empty array
        sendJSONResponse($response, array());
    } catch (Exception $e) {
        sendJSONError($response, $e->getMessage());
    }
});

//================================================================================
// API: Authenticate (get device token with valid username/password/secret chars)
//      Input JSON fields: 'username', 'password', 'security'
//      Output JSON fields: 'deviceToken'
//================================================================================
$this->respond('POST', '/authenticate', function ($request, $response, $service) {
    error_log(TAG . 'Login request from ' . $_SERVER['REMOTE_ADDR']);

    // Make sure a username and password have been supplied
    $loginData = json_decode($request->body(), true);

    if (!$loginData || !isset($loginData['username']) || !isset($loginData['password'])) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Username/password not supplied');
        sendJSONError($response, 'Your device did not supply a username and/or password.');
    }
    
    // Ensure a security code was also provided.
    if (!$loginData || !isset($loginData['secureChars'])) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Security code not supplied');
        sendJSONError($response, 'Your device did not supply a security code.');
    }

    // Attempt login
    $user = R::findOne('user', 'email = ?', array($loginData['username']));
    
    // Check username/password
    if (is_null($user) || !password_verify($loginData['password'], $user->password)) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Incorrect username or password.');
        sendJSONError($response, 'Incorrect username or password.');
        return;
    }
    
    // Get array within security field of the original JSON body
    $securityData = $loginData['secureChars'];
    
    // Boolean to represent whether a security code was successfully validated
    $validated = false;
    
    // Get the set security code
    $securityCode = $user->security;

    // For every security digit, validate them
    foreach($securityData as $secureIndex => $secureChars){
        // Check if the characters are the same
        if($securityCode[$secureIndex-1] ===  $secureChars){
            $validated = true;
            continue;
        } else {
            // If at any point the validation fails, then exit the loop and mark the validation as failed.
            $validated = false;
            break;
        }
    }
    
    if (!$validated) {
        error_log(TAG . 'Access denied to ' . $_SERVER['REMOTE_ADDR'] . ': Incorrect security code digits.');
        sendJSONError($response, 'Incorrect security code digits.');
    } else {
        // Login successful; create and store device token information
        $deviceToken = R::dispense('devicetoken');
        $deviceToken->token = generateDeviceTokenHash($loginData);
        updateDeviceToken($deviceToken);

        // Associate token with user
        $user->xownDevicetokenList[] = $deviceToken;
        R::store($user);

        error_log(TAG . 'Access granted to ' . $_SERVER['REMOTE_ADDR'] . ': \'' . $loginData['username'] . '\' got device token ' . $deviceToken->token);
        sendJSONResponse($response, array('deviceToken' => $deviceToken->token));
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
    $user = R::load('user', $deviceToken->user_id);

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
        sendJSONError($response, 'The API key supplied by your device was invalid. Please update your app, or contact support.');
    }

    $user = checkDeviceToken();
    if (is_null($user)) {
        sendJSONError($response, 'Your device did not supply a valid authentication token.');
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

function sendJSONResponse($response, $data) {
    $body = array_merge(
        $data,
        array('status' => API_SUCCESS)
    );

    $response->body(json_encode($body));
    $response->send();
    exit;
}

function sendJSONError($response, $reason) {
    $body = array(
        'errorMessage' => $reason,
        'status' => API_ERROR
    );

    $response->body(json_encode($body));
    $response->send();
    exit;
}

// Send a push notification to one or more registered devices using Google Cloud Messaging
function sendGoogleCloudMessage($message, $ids) {
    // URL to GCM API
    $url = 'https://android.googleapis.com/gcm/send';

    // POST variables
    $post = array(
        'registration_ids'  => $ids,
        'data' => array('message' => $message)
    );

    // HTTP request headers
    $headers = array('Authorization: key=' . GOOGLE_SERVER_API_KEY, 'Content-Type: application/json');

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
    $errno = curl_errno($ch);
    $errmsg = curl_error($ch);

    // Close curl handle
    curl_close($ch);

    // If there was an error, return it, otherwise return Google response
    return $errno ? $errmsg : $result;
}