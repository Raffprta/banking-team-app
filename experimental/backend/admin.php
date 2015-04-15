<?php

//================================================================================
//
// CSC2022 Team Project - Team 19 - Lloyds App Backend
//
// admin.php - administrator functionality
//
//================================================================================

// TODO: Bank account creation/management
// TODO: Basic admin pages showing accounts and users
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

    $usersArray = array();
    foreach ($users as $user) {
        $usersArray[] = userToArray($user);
    }

    try {
        displayPage('users.twig', array('users' => $usersArray));
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
        $userBean = R::findOne('user', 'id = ?', array($request->userId));
        if (!is_null($userBean)) {
            $user = userToArray($userBean);

            displayPage('edituser.twig', array_merge(array('editMode' => true), $user));
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
        $recipients = R::find('user', 'device_id IS NOT NULL');
        displayPage('pushmessages.twig', array('recipients' => $recipients));
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
    $errorMessages = validateRegistrationData($userData, true);

    // Check if user already exists
    if (emailAddressExists($userData['email'])) {
        $errorMessages[] = 'An account already exists for the email address \'' . $userData['email'] . '\'.';
        logActivity('Account creation failed: account already exists for the email address \'' . $userData['email'] . '\'.');
    }

    // Display any form errors and re-populate registration form
    if (count($errorMessages) > 0) {
        displayPage('edituser.twig', array(
                'errorMessages' => $errorMessages,

                // Pass the bad data back to the form so the user can amend it
                'firstName' => $userData['firstName'],
                'surname' => $userData['surname'],
                'email' => $userData['email'],
                'password' => $userData['password'],
                'passwordVerify' => $userData['passwordVerify']
            )
        );
        logActivity('Account creation failed: validation errors.');
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

            logActivity('Account creation succeeded (' . $userData['email'] . ').');
        } catch (Exception $e) {
            displayError($e->getMessage());
            logActivity('Account creation failed because an exception was thrown: ' . $e->getMessage());
        }
    }
});

//================================================================================
// Admin Action: Edit user
//================================================================================
$this->respond('POST', '/edituser/[i:userId]', function ($request, $response, $service) {
    checkAccessLevel(ACCESS_LEVEL_ADMINISTRATOR);

    $updatedUserData = formatUserData($_POST);

    // Only validate passwords if both form fields were empty
    $hasNewPassword = empty($updatedUserData['password']) && empty($updatedUserData['passwordVerify']) ? false : true;

    $errorMessages = validateRegistrationData($updatedUserData, $hasNewPassword);

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