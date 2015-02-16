<?php

//================================================================================
//
// CSC2022 Team Project - Team 19 - Lloyds App Backend
//
// admin.php - administrator functionality
//
//================================================================================

// TODO: User creation/management, bank account creation/management
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