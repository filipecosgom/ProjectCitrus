package pt.uc.dei.config;

public class MessageTemplate {

    /**
     * The email template for account activation.
     * Contains a link that allows users to activate their account.
     */
    public static final String ACCOUNT_ACTIVATION_TEMPLATE_EN(String activationLink, Integer expirationTime, String twoFactorSecret) {
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
          .secret-key-box {
            background-color: #eee;
            padding: 10px;
            font-weight: bold;
            text-align: center;
            border-radius: 5px;
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

          <h3>Set Up Two-Factor Authentication (2FA)</h3>
          <p>For added security, your account requires Two-Factor Authentication.</p>
          <p>Follow these steps to set it up:</p>
          <ol>
            <li>Open your authenticator app (Google Authenticator, Microsoft Authenticator, or Authy).</li>
            <li>Tap **Add a new account**, then select **Manual Entry**.</li>
            <li>Enter the secret key below:</li>
          </ol>
          <div class="secret-key-box">
            %s
          </div>
          <p>Save the setup, and your app will start generating 6-digit codes every 30 seconds.</p>
          <p>When logging in, you must enter the latest code from your authenticator app.</p>

          <p>If you did not request this activation, please ignore this email.</p>
          <p>Kind regards,<br />The CITRUS Team</p>
          <div class="footer">
            ©CITRUS. All rights reserved.
          </div>
        </div>
      </body>
    </html>
    """.formatted(activationLink, expirationTime, twoFactorSecret);
    }

    public static final String ACCOUNT_ACTIVATION_TEMPLATE_PT(String activationLink, Integer expirationTime, String twoFactorSecret) {
        return """
    <!DOCTYPE html>
    <html lang="pt">
      <head>
        <meta charset="UTF-8" />
        <title>Ativação de Conta</title>
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
          .secret-key-box {
            background-color: #eee;
            padding: 10px;
            font-weight: bold;
            text-align: center;
            border-radius: 5px;
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
          <h2>Ative a Sua Conta</h2>
          <p>Olá,</p>
          <p>
            Bem-vindo! Para concluir o seu registo, ative a sua conta clicando no botão abaixo:
          </p>
          <p style="text-align: center;">
            <a href="%s" class="button">Ativar Conta</a>
          </p>
          <p>Este link será válido por %d horas.</p>

          <h3>Configurar a Autenticação de Dois Fatores (2FA)</h3>
          <p>Para aumentar a segurança da sua conta, é necessário ativar a autenticação de dois fatores.</p>
          <p>Siga os passos abaixo para configurá-la:</p>
          <ol>
            <li>Abra a aplicação autenticadora (Google Authenticator, Microsoft Authenticator ou Authy).</li>
            <li>Toque em **Adicionar nova conta** e escolha **Entrada Manual**.</li>
            <li>Introduza a seguinte chave secreta:</li>
          </ol>
          <div class="secret-key-box">
            %s
          </div>
          <p>Guarde a configuração e a aplicação começará a gerar códigos de 6 dígitos a cada 30 segundos.</p>
          <p>Quando fizer login, será necessário introduzir o código mais recente da aplicação autenticadora.</p>

          <p>Se não solicitou esta ativação, ignore este email.</p>
          <p>Atenciosamente,<br />A Equipa CITRUS</p>
          <div class="footer">
            ©CITRUS. Todos os direitos reservados.
          </div>
        </div>
      </body>
    </html>
    """.formatted(activationLink, expirationTime, twoFactorSecret);
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