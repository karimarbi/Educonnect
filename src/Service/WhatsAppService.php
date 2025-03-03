<?php


namespace App\Service;

use Symfony\Contracts\HttpClient\HttpClientInterface;

class WhatsAppService
{
    private $httpClient;
    private $instanceId;
    private $token;

    public function __construct(HttpClientInterface $httpClient)
    {
        $this->httpClient = $httpClient;
        $this->instanceId = 'instance109173'; // Remplace avec ton ID
        $this->token = '15njx8fhrqe297bj'; // Remplace avec ton token
    }

    public function sendMessage(string $phoneNumber, string $message): bool
    {
        $url = "https://api.ultramsg.com/{$this->instanceId}/messages/chat";
        $response = $this->httpClient->request('POST', $url, [
            'json' => [
                'token' => $this->token,
                'to' => $phoneNumber,
                'body' => $message
            ]
        ]);

        return $response->getStatusCode() === 200;
    }
}
