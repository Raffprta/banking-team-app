<?php

//================================================================================
//
// CSC2022 Team Project - Team 19 - Lloyds App Backend
//
// admin.php - administrator functionality
//
//================================================================================

// TODO: Transaction simulation methods

//================================================================================
// Admin: PHPInfo
//================================================================================
$this->respond('GET', '/phpinfo', function () {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);
    phpinfo();
});

//================================================================================
// Admin: User management
//================================================================================
$this->respond('GET', '/users', function () {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    $users = R::findAll('user');

    try {
        displayPage('users.twig', array('users' => $users));
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin: Create user
//================================================================================
$this->respond('GET', '/createuser', function () {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        displayPage('edituser.twig', null);
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin: Edit user
//================================================================================
$this->respond('GET', '/edituser/[i:userId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        $user = R::findOne('user', 'id = ?', array($request->userId));
        if (!is_null($user)) {
            displayPage('edituser.twig', array('editMode' => true, 'user' => $user));
        } else {
            displayError('User not found.');
        }
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin: Send push messages page
//================================================================================
$this->respond('GET', '/pushmessages', function () {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        $recipients = R::find('user', 'gcm_id IS NOT NULL');
        displayPage('pushmessages.twig', array('recipients' => $recipients));
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin Action: Send push messages
//================================================================================
$this->respond('POST', '/pushmessages', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        // Validate recipients and message.
        $errorMessages = array();

        if (!isset($_POST['recipients'])) {
            $errorMessages[] = 'Please select one or more recipients for the push message.';
        }

        if (!isset($_POST['message']) || empty($_POST['message'])) {
            $errorMessages[] = 'Please enter a message to send.';
        }

        if (!isset($_POST['messageType']) || $_POST['messageType'] !== PUSH_TYPE_INFO && $_POST['messageType'] !== PUSH_TYPE_OFFERS && $_POST['messageType'] !== PUSH_TYPE_HEARTBEAT) {
            $errorMessages[] = 'Invalid message type.';
        }

        // Get all potential recipients from the database
        $recipientBeans = R::find('user', 'gcm_id IS NOT NULL');

        if (count($errorMessages) === 0) {
            // Variables from form POST
            $recipients = $_POST['recipients'];
            $message = trim($_POST['message']);
            $messageType = $_POST['messageType'];

            $recipientGcmIds = array();

            // Match checked recipients with database recipients
            foreach ($recipients as $id => $value) {
                // If checked, add their GCM ID to the array
                if ($value === "on") {
                    foreach ($recipientBeans as $recipientBean) {
                        if ($recipientBean->id == $id)
                            $recipientGcmIds[] = $recipientBean->gcmId;
                    }
                }
            }

            // Construct JSON message
            $jsonMessage = json_encode(array(
                'messageType' => $messageType,
                'message' => $message
            ));

            // Send the messages
            $gcmResult = sendGoogleCloudMessage($jsonMessage, $recipientGcmIds);
            $decodedResult = json_decode($gcmResult);

            // Assume a non-JSON return value is some kind of curl error
            if (is_null($decodedResult)) {
                $errorMessages[] = 'curl error: ' . $gcmResult;
            } else {
                $successes = $decodedResult->success;
                $failures = $decodedResult->failure;

                if ($failures) {
                    $errorMessages[] = sprintf('Couldn\'t send message to %d %s.', $failures, $failures == 1 ? 'user' : 'users');
                }

                if ($successes) {
                    $service->flash(sprintf('Your message has been sent to %d %s.', $successes, $successes == 1 ? 'user' : 'users'), FLASH_SUCCESS);
                }
            }
        }

        displayPage('pushmessages.twig', array('recipients' => $recipientBeans, 'errorMessages' => $errorMessages));
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin: Bank account management
//================================================================================
$this->respond('GET', '/bankaccounts/[i:userId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        $user = R::findOne('user', 'id = ?', array($request->userId));
        if (!is_null($user)) {
            displayPage('bankaccounts.twig', array('user' => $user, 'accounts' => $user->xownAccountList));
        } else {
            displayError('User not found.');
        }
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin: Create bank account
//================================================================================
$this->respond('GET', '/createbankaccount/[i:userId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        $user = R::findOne('user', 'id = ?', array($request->userId));
        if (!is_null($user)) {
            displayPage('editbankaccount.twig', array('user' => $user));
        } else {
            displayError('User not found.');
        }
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin: Edit bank account
//================================================================================
$this->respond('GET', '/editbankaccount/[i:accountId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        $account = R::findOne('account', 'id = ?', array($request->accountId));

        if (!is_null($account)) {
            displayPage('editbankaccount.twig', array('editMode' => true, 'account' => $account, 'user' => $account->user));
        } else {
            displayError('Account not found.');
        }
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin Action: Create bank account
//================================================================================
$this->respond('POST', '/createbankaccount/[i:userId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        $user = R::findOne('user', 'id = ?', array($request->userId));

        // Bail out if the user isn't valid
        if (is_null($user)) {
            displayError('User not found.');
            exit;
        } else {
            $bankAccountData = formatBankAccountData($_POST);
            $errorMessages = validateBankAccountData($bankAccountData, false);

            // Display any form errors and re-populate registration form
            if (count($errorMessages) > 0) {
                displayPage('editbankaccount.twig', array(
                        'user' => $user,
                        'errorMessages' => $errorMessages,

                        // Pass any bad data back to the form so it can be amended
                        'account' => $bankAccountData
                    )
                );
                logActivity('Bank account creation failed: validation errors.');
            }

            // We're good, notify success and display correct page
            else {
                // Create the bank account
                createBankAccount($user, $bankAccountData);

                // Flash success message
                $service->flash('The bank account has been created.', FLASH_SUCCESS);

                // Return to edit bank account page
                $service->back();
                $response->send();

                logActivity('Bank account creation succeeded ('
                    . $bankAccountData['accountNumber']
                    . '/'
                    . $bankAccountData['sortCode1']
                    . '-'
                    . $bankAccountData['sortCode2']
                    . '-'
                    . $bankAccountData['sortCode3']
                    . ').');
            }
        }
    } catch (Exception $e) {
        displayError($e->getMessage());
        logActivity('Bank account creation failed because an exception was thrown: ' . $e->getMessage());
    }
});

//================================================================================
// Admin: Create bank account
//================================================================================
$this->respond('GET', '/editbankaccount', function () {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        displayPage('editbankaccount.twig', null);
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin Action: Create user account
//================================================================================
$this->respond('POST', '/createuser', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    $userData = formatUserData($_POST);
    $errorMessages = validateRegistrationData($userData, true, true);

    // Check if user already exists
    if (emailAddressExists($userData['email'])) {
        $errorMessages[] = 'An account already exists for the email address \'' . $userData['email'] . '\'.';
        logActivity('User account creation failed: account already exists for the email address \'' . $userData['email'] . '\'.');
    }

    // Display any form errors and re-populate registration form
    if (count($errorMessages) > 0) {
        displayPage('edituser.twig', array(
                'errorMessages' => $errorMessages,

                // Pass the bad data back to the form so the user can amend it
                'user' => $userData
            )
        );
        logActivity('User account creation failed: validation errors.');
    }

    // We're good, present user with login form and success notice
    else {
        try {
            // Create the user
            createUser($userData, false);

            // Pass success message to login form
            $service->flash('The account has been created. The user may now log in.', FLASH_SUCCESS);

            // Return to edit user page
            $service->back();
            $response->send();

            logActivity('User account creation succeeded (' . $userData['email'] . ').');
        } catch (Exception $e) {
            displayError($e->getMessage());
            logActivity('User account creation failed because an exception was thrown: ' . $e->getMessage());
        }
    }
});

//================================================================================
// Admin Action: Edit user
//================================================================================
$this->respond('POST', '/edituser/[i:userId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    $updatedUserData = formatUserData($_POST);

    // Only validate passwords or security prompts if both form fields were empty
    $hasNewPassword = empty($updatedUserData['password']) && empty($updatedUserData['passwordVerify']) ? false : true;
    $hasNewSecurity = empty($updatedUserData['security']) && empty($updatedUserData['securityVerify']) ? false : true;

    $errorMessages = validateRegistrationData($updatedUserData, $hasNewPassword, $hasNewSecurity);

    // Check the new email address isn't already in use
    if (R::findOne('user', 'id != ? and email = ?', array($request->userId, $updatedUserData['email'])) !== null) {
        $errorMessages[] = 'The email address \'' . $updatedUserData['email'] . '\' is already in use.';
    }

    // Display any validation errors
    if (count($errorMessages) > 0) {
        // Pass validation errors to edit user page
        foreach ($errorMessages as $errorMessage) {
            $service->flash($errorMessage, FLASH_ERROR);
        }
    }

    // Validation succeeded
    else {
        try {
            $userBean = R::findOne('user', 'id = ?', array($request->userId));
            if (!is_null($userBean)) {
                $userBean->firstName = $updatedUserData['firstName'];
                $userBean->surname = $updatedUserData['surname'];
                $userBean->email = $updatedUserData['email'];
                if ($hasNewPassword === true) {
                    $userBean->password = password_hash($updatedUserData['password'], PASSWORD_DEFAULT);
                }
				
				if ($hasNewSecurity === true) {
                    $userBean->security = password_hash($updatedUserData['security'], PASSWORD_DEFAULT);
                }
				
                R::store($userBean);

                // Pass success message to edit user page
                $service->flash('The user\'s details were successfully updated.', FLASH_SUCCESS);
            } else {
                displayError('User not found.');
                exit;
            }
        } catch (Exception $e) {
            displayError($e->getMessage());
            exit;
        }
    }

    // Return to edit user page
    $service->back();
    $response->send();
});

//================================================================================
// Admin Action: Edit bank account
//================================================================================
$this->respond('POST', '/editbankaccount/[i:accountId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    $updatedBankAccountData = formatBankAccountData($_POST);
    $errorMessages = validateBankAccountData($updatedBankAccountData, true);

    // Display any validation errors
    if (count($errorMessages) > 0) {
        // Pass validation errors to edit user page
        foreach ($errorMessages as $errorMessage) {
            $service->flash($errorMessage, FLASH_ERROR);
        }
    }

    // Validation succeeded
    else {
        try {
            $accountBean = R::findOne('account', 'id = ?', array($request->accountId));
            if (!is_null($accountBean)) {
                $accountBean->accountType = $updatedBankAccountData['accountType'];
                $accountBean->nickname = $updatedBankAccountData['nickname'];
                $accountBean->accountNumber = $updatedBankAccountData['accountNumber'];
                $accountBean->sortCode = $updatedBankAccountData['sortCode'];
                $accountBean->interest = $updatedBankAccountData['interest'];
                $accountBean->overdraft = $updatedBankAccountData['overdraft'] * 100;
                R::store($accountBean);

                // Pass success message to edit user page
                $service->flash('The bank account was successfully updated.', FLASH_SUCCESS);
            } else {
                displayError('Bank account not found.');
                exit;
            }
        } catch (Exception $e) {
            displayError($e->getMessage());
            exit;
        }
    }

    // Return to edit bank account page
    $service->back();
    $response->send();
});

//================================================================================
// Admin Action: Delete user
//================================================================================
$this->respond('GET', '/deleteuser/[i:userId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        $userBean = R::findOne('user', 'id = ?', array($request->userId));
        if (!is_null($userBean)) {
            // Delete record
            R::trash($userBean);

            // Show success message and redirect to previous page
            $service->flash('User ' . $userBean->email . ' deleted successfully.', FLASH_SUCCESS);
            $service->back();
            $response->send();
        } else {
            displayError('User not found.');
        }
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin Action: Delete bank account
//================================================================================
$this->respond('GET', '/deletebankaccount/[i:accountId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        $accountBean = R::findOne('account', 'id = ?', array($request->accountId));
        if (!is_null($accountBean)) {
            $id = $accountBean->id;

            // Delete record
            R::trash($accountBean);

            // Show success message and redirect to previous page
            $service->flash('Bank account ID ' . $id . ' deleted successfully.', FLASH_SUCCESS);
            $service->back();
            $response->send();
        } else {
            displayError('Bank account not found.');
        }
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin: View Activity Log
//================================================================================
$this->respond('GET', '/activitylog', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);
    // Order the events descending and limit them by 100
    $sql = 'ORDER BY id DESC LIMIT 100';

    try {
        // Create an array of beans with the last 100 events
        $eventBeans = R::findAll('logevent', $sql);

        // Display the page of events
        displayPage('activitylog.twig', array('events' => $eventBeans));
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});

//================================================================================
// Admin Action: Clear Activity Log
//================================================================================
$this->respond('GET', '/clearactivitylog', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    try {
        // Find and delete all events
        $eventBeans = R::findAll('logevent');
        R::trashAll($eventBeans);

        // Redirect back to activity log page
        $service->flash('The activity log was cleared.', FLASH_INFO);
        $response->redirect('/admin/activitylog');
        $response->send();
    } catch (Exception $e) {
        displayError($e->getMessage());
    }
});