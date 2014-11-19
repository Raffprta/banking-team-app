<?php

/*
 * PHP Google Cloud Messaging example script - taken from:
 * http://stackoverflow.com/questions/11242743/gcm-with-php-google-cloud-messaging
 */

// Array of key/values that will be sent to the device,
// accessible by Intent extras.
$data = array( 'message' => 'Hello World!' );

// Array of device IDs that will receive this message -
// these will be stored in DB alongside user account data
$ids = array( 'APA91bHLkz-TCEJ5mO6WmT2nucdx4yIE5HMLcg683z-SUmxkJnIkvovk2U8VuLQF32WQC8NXISTq1AvkPbf_figUyFfzzPxkXpQZbuq70u4lQSTVk7DQd0-2-upa4gmbF8SDk_wXmiYc1KNuOGtNXmb1kRZOs1RH1EUgaSXbYlzdYYnS87VsQOg' );

// Send the message
sendGoogleCloudMessage($data, $ids);

function sendGoogleCloudMessage( $data, $ids )
{
    // API key from Google Developer console
    $apiKey = 'AIzaSyAnOqe2hy_znuRnk6pfijuHXOFBH_Z6r6M';

    // URL to GCM API
    $url = 'https://android.googleapis.com/gcm/send';

    // POST variables
    $post = array(  'registration_ids'  => $ids,
                    'data'              => $data );

    // HTTP request headers
    $headers = array( 'Authorization: key=' . $apiKey,
                      'Content-Type: application/json' );

    // Initialize curl
    $ch = curl_init();

    // Set URL to GCM API URL
    curl_setopt( $ch, CURLOPT_URL, $url );

    // Set request method to POST
    curl_setopt( $ch, CURLOPT_POST, true );

    // Set custom headers
    curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers );

    // Get the response back as string instead of printing it
    curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );

    // Set post data as JSON
    curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $post ) );

    // Send the push!
    $result = curl_exec( $ch );

    // Error? Display it!
    if ( curl_errno( $ch ) )
    {
        echo 'GCM error: ' . curl_error( $ch );
    }

    // Close curl handle
    curl_close( $ch );

    // Debug GCM response
    echo $result;
}
