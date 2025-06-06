export const apiBaseUrl = 'https://localhost:8443/projectcitrus/rest';

export const httpStatusCodes = {
    success: 200, // OK
    created: 201, // Created
    accepted: 202, // Accepted
    noContent: 204, // No Content
    badRequest: 400, // Bad Request
    unauthorized: 401, // Unauthorized
    errorForbidden: 403, // errorForbidden
    notFound: 404, // Not Found
    methodNotAllowed: 405, // Method Not Allowed
    conflict: 409, // Conflict
    internalServerError: 500, // Internal Server Error
    notImplemented: 501, // Not Implemented
    badGateway: 502, // Bad Gateway
    serviceUnavailable: 503, // Service Unavailable
    gatewayTimeout: 504 // Gateway Timeout
}