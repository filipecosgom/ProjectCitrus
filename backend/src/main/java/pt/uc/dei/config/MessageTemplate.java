package pt.uc.dei.config;

public class MessageTemplate {

    /**
     * The email template for account activation.
     * Contains a link that allows users to activate their account.
     */
    public static final String ACCOUNT_ACTIVATION_TEMPLATE(String activationLink, Integer expirationTime) {
        return """
        <!DOCTYPE html>
        <html lang="en">
          <head>
            <meta charset="UTF-8" />
            <title>Account Activation</title>
            <style>
              body {
                font-family: Arial, sans-serif;
                background-color: #f4f4f4;
                padding: 20px;
                margin: 0;
              }
              .email-container {
                max-width: 600px;
                background-color: #ffffff;
                margin: auto;
                padding: 30px;
                border-radius: 8px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
              }
              .button {
                display: inline-block;
                padding: 12px 24px;
                margin: 20px 0;
                color: #ffffff !important;
                background-color: #2F7C9C;
                text-decoration: none;
                border-radius: 5px;
                font-weight: bold;
              }
              .footer {
                font-size: 12px;
                color: #777777;
                margin-top: 30px;
                text-align: center;
              }
            </style>
          </head>
          <body>
            <div class="email-container">
              <h2>Activate Your Account</h2>
              <p>Hi,</p>
              <p>
                Welcome! To complete your registration, please activate your account using the button below:
              </p>
              <p style="text-align: center;">
                <a href="%s" class="button">Activate Your Account</a>
              </p>
              <p>This link will remain valid for %d hours.</p>
              <p>
                If you did not request this activation, please ignore this email.
              </p>
              <p>Kind regards,<br />The CITRUS Team</p>
              <div class="footer">
                ©CITRUS. All rights reserved.
              </div>
            </div>
          </body>
        </html>
    """.formatted(activationLink, expirationTime);
    }

    /**
     * The email template for account activation.
     * Contains a link that allows users to activate their account.
     */
    public static final String PASSWORD_RESET_TEMPLATE(String resetLink, Integer expirationTime) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8" />
                    <title>Password Reset Request</title>
                    <style>
                      body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                        padding: 20px;
                        margin: 0;
                      }
                      .email-container {
                        max-width: 600px;
                        background-color: #ffffff;
                        margin: auto;
                        padding: 30px;
                        border-radius: 8px;
                        box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
                      }
                      .button {
                        display: inline-block;
                        padding: 12px 24px;
                        margin: 20px 0;
                        color: #ffffff;
                        background-color: #9c2f31;
                        text-decoration: none;
                        border-radius: 5px;
                        font-weight: bold;
                      }
                      .footer {
                        font-size: 12px;
                        color: #777777;
                        margin-top: 30px;
                        text-align: center;
                      }
                    </style>
                  </head>
                  <body>
                    <div class="email-container">
                      <h2>Password Reset Request</h2>
                      <p>Hi,</p>
                      <p>
                        We have received a request to reset the password associated with your account.  
                        If you initiated this request, please click the button below to proceed:
                      </p>
                      <p style="text-align: center;">
                        <a href="%s" class="button" style="color: #ffffff !important; text-decoration: none;">Reset Your Password</a>
                      </p>
                      <p>This link will remain valid for %d hours.</p>
                      <p>
                        If you did not request a password reset, please disregard this email.  
                        Your account will remain secure.
                      </p>
                      <p>Kind regards,<br />The CITRUS Team</p>
                      <div class="footer">
                        ©CITRUS. All rights reserved.
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(resetLink, expirationTime);
    }
}