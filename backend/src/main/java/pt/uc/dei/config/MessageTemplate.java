package pt.uc.dei.config;

public class MessageTemplate {

  public static final String ACCOUNT_ACTIVATION_TEMPLATE_EN(String activationLink, Integer expirationTime,
      String twoFactorSecret) {
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

  public static final String ACCOUNT_ACTIVATION_TEMPLATE_PT(String activationLink, Integer expirationTime,
      String twoFactorSecret) {
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
  public static final String PASSWORD_RESET_TEMPLATE_EN(String resetLink, Integer expirationTime) {
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
        """
        .formatted(resetLink, expirationTime);
  }

  public static final String PASSWORD_RESET_TEMPLATE_PT(String resetLink, Integer expirationTime) {
    return """
        <!DOCTYPE html>
        <html lang="pt">
          <head>
            <meta charset="UTF-8" />
            <title>Solicitação de Redefinição de Senha</title>
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
              <h2>Solicitação de Redefinição de Senha</h2>
              <p>Olá,</p>
              <p>
                Recebemos uma solicitação para redefinir a senha associada à sua conta.
                Se você iniciou esta solicitação, clique no botão abaixo para continuar:
              </p>
              <p style="text-align: center;">
                <a href="%s" class="button" style="color: #ffffff !important; text-decoration: none;">Redefinir sua senha</a>
              </p>
              <p>Este link será válido por %d horas.</p>
              <p>
                Se você não solicitou a redefinição de senha, ignore este e-mail.
                Sua conta continuará segura.
              </p>
              <p>Atenciosamente,<br />Equipe CITRUS</p>
              <div class="footer">
                ©CITRUS. Todos os direitos reservados.
              </div>
            </div>
          </body>
        </html>
        """
        .formatted(resetLink, expirationTime);
  }

  /**
   * Email template for new cycle notification (English).
   */
  public static final String CYCLE_NOTIFICATION_TEMPLATE_EN(String cycleId, String startDate, String endDate,
      String adminName, int appraisalsCount, String cycleLink) {
    return """
        <!DOCTYPE html>
        <html lang="en">
          <head>
            <meta charset="UTF-8" />
            <title>New Performance Cycle Started</title>
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
                background-color: #00b9cd;
                text-decoration: none;
                border-radius: 5px;
                font-weight: bold;
              }
              .cycle-info {
                background-color: #f8f9fa;
                padding: 20px;
                border-radius: 5px;
                margin: 20px 0;
              }
              .highlight {
                color: #00b9cd;
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
              <h2>New Performance Cycle Started</h2>
              <p>Hello,</p>
              <p>
                A new performance evaluation cycle has been created and is now active.
                As a manager/administrator, we're notifying you of this important update.
              </p>

              <div class="cycle-info">
                <h3>Cycle Details:</h3>
                <p><strong>Cycle ID:</strong> %s</p>
                <p><strong>Start Date:</strong> %s</p>
                <p><strong>End Date:</strong> %s</p>
                <p><strong>Created by:</strong> %s</p>
                <p><strong>Total Appraisals:</strong> <span class="highlight">%d evaluations</span> are now in progress</p>
              </div>

              <p>
                Please review the active evaluations and ensure your team members
                complete their assessments within the specified timeframe.
              </p>

              <p style="text-align: center;">
                <a href="%s" class="button">View Cycle Details</a>
              </p>

              <p>
                For any questions or support, please contact your system administrator.
              </p>

              <p>Best regards,<br />The CITRUS Team</p>
              <div class="footer">
                ©CITRUS. All rights reserved.
              </div>
            </div>
          </body>
        </html>
        """
        .formatted(cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
  }

  /**
   * Email template for new cycle notification (Portuguese).
   */
  public static final String CYCLE_NOTIFICATION_TEMPLATE_PT(String cycleId, String startDate, String endDate,
      String adminName, int appraisalsCount, String cycleLink) {
    return """
        <!DOCTYPE html>
        <html lang="pt">
          <head>
            <meta charset="UTF-8" />
            <title>Novo Ciclo de Avaliação Iniciado</title>
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
                background-color: #00b9cd;
                text-decoration: none;
                border-radius: 5px;
                font-weight: bold;
              }
              .cycle-info {
                background-color: #f8f9fa;
                padding: 20px;
                border-radius: 5px;
                margin: 20px 0;
              }
              .highlight {
                color: #00b9cd;
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
              <h2>Novo Ciclo de Avaliação Iniciado</h2>
              <p>Olá,</p>
              <p>
                Um novo ciclo de avaliação de desempenho foi criado e está agora ativo.
                Como gestor/administrador, estamos a notificá-lo desta importante atualização.
              </p>

              <div class="cycle-info">
                <h3>Detalhes do Ciclo:</h3>
                <p><strong>ID do Ciclo:</strong> %s</p>
                <p><strong>Data de Início:</strong> %s</p>
                <p><strong>Data de Fim:</strong> %s</p>
                <p><strong>Criado por:</strong> %s</p>
                <p><strong>Total de Avaliações:</strong> <span class="highlight">%d avaliações</span> estão em progresso</p>
              </div>

              <p>
                Por favor, reveja as avaliações ativas e garanta que os membros da sua equipa
                completam as suas avaliações dentro do prazo especificado.
              </p>

              <p style="text-align: center;">
                <a href="%s" class="button">Ver Detalhes do Ciclo</a>
              </p>

              <p>
                Para qualquer questão ou apoio, contacte o administrador do sistema.
              </p>

              <p>Atenciosamente,<br />A Equipa CITRUS</p>
              <div class="footer">
                ©CITRUS. Todos os direitos reservados.
              </div>
            </div>
          </body>
        </html>
        """
        .formatted(cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
  }

  /**
   * Email template for cycle ending notification (English).
   */
  public static final String CYCLE_END_NOTIFICATION_TEMPLATE_EN(String cycleId, String startDate, String endDate,
      String adminName, int appraisalsCount, String cycleLink) {
    return """
        <!DOCTYPE html>
        <html lang=\"en\">
          <head>
            <meta charset=\"UTF-8\" />
            <title>Performance Cycle Ended</title>
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
                background-color: #00b9cd;
                text-decoration: none;
                border-radius: 5px;
                font-weight: bold;
              }
              .cycle-info {
                background-color: #f8f9fa;
                padding: 20px;
                border-radius: 5px;
                margin: 20px 0;
              }
              .highlight {
                color: #00b9cd;
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
            <div class=\"email-container\">
              <h2>Performance Cycle Ended</h2>
              <p>Hello,</p>
              <p>
                The performance evaluation cycle below has ended. Please review the results and take any necessary actions.
              </p>
              <div class=\"cycle-info\">
                <h3>Cycle Details:</h3>
                <p><strong>Cycle ID:</strong> %s</p>
                <p><strong>Start Date:</strong> %s</p>
                <p><strong>End Date:</strong> %s</p>
                <p><strong>Created by:</strong> %s</p>
                <p><strong>Total Appraisals:</strong> <span class=\"highlight\">%d evaluations</span> were included in this cycle</p>
              </div>
              <p>
                You can view the cycle summary and download reports using the link below.
              </p>
              <p style=\"text-align: center;\">
                <a href=\"%s\" class=\"button\">View Cycle Results</a>
              </p>
              <p>
                For any questions or support, please contact your system administrator.
              </p>
              <p>Best regards,<br />The CITRUS Team</p>
              <div class=\"footer\">
                ©CITRUS. All rights reserved.
              </div>
            </div>
          </body>
        </html>
        """
        .formatted(cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
  }

  /**
   * Email template for cycle ending notification (Portuguese).
   */
  public static final String CYCLE_END_NOTIFICATION_TEMPLATE_PT(String cycleId, String startDate, String endDate,
      String adminName, int appraisalsCount, String cycleLink) {
    return """
        <!DOCTYPE html>
        <html lang=\"pt\">
          <head>
            <meta charset=\"UTF-8\" />
            <title>Ciclo de Avaliação Encerrado</title>
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
                background-color: #00b9cd;
                text-decoration: none;
                border-radius: 5px;
                font-weight: bold;
              }
              .cycle-info {
                background-color: #f8f9fa;
                padding: 20px;
                border-radius: 5px;
                margin: 20px 0;
              }
              .highlight {
                color: #00b9cd;
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
            <div class=\"email-container\">
              <h2>Ciclo de Avaliação Encerrado</h2>
              <p>Olá,</p>
              <p>
                O ciclo de avaliação de desempenho abaixo foi encerrado. Por favor, reveja os resultados e tome as ações necessárias.
              </p>
              <div class=\"cycle-info\">
                <h3>Detalhes do Ciclo:</h3>
                <p><strong>ID do Ciclo:</strong> %s</p>
                <p><strong>Data de Início:</strong> %s</p>
                <p><strong>Data de Fim:</strong> %s</p>
                <p><strong>Criado por:</strong> %s</p>
                <p><strong>Total de Avaliações:</strong> <span class=\"highlight\">%d avaliações</span> foram incluídas neste ciclo</p>
              </div>
              <p>
                Pode visualizar o resumo do ciclo e baixar relatórios usando o link abaixo.
              </p>
              <p style=\"text-align: center;\">
                <a href=\"%s\" class=\"button\">Ver Resultados do Ciclo</a>
              </p>
              <p>
                Para qualquer questão ou apoio, contacte o administrador do sistema.
              </p>
              <p>Atenciosamente,<br />A Equipa CITRUS</p>
              <div class=\"footer\">
                ©CITRUS. Todos os direitos reservados.
              </div>
            </div>
          </body>
        </html>
        """
        .formatted(cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
  }

  /**
   * Email template for notifying managers when a managed user updates their
   * profile (English).
   */
  public static String PROFILE_UPDATE_NOTIFICATION_TEMPLATE_EN(String managerName, String userName, String updateDate,
      String profileLink) {
    return """
        <!DOCTYPE html>
        <html lang=\"en\">
          <head>
            <meta charset=\"UTF-8\" />
            <title>Profile Updated</title>
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
            <div class=\"email-container\">
              <h2>Profile Updated</h2>
              <p>Hello %s,</p>
              <p>
                This is to inform you that your team member <strong>%s</strong> has updated their profile on %s.
              </p>
              <p>
                You can review the updated profile using the link below:
              </p>
              <p style=\"text-align: center;\">
                <a href=\"%s\" class=\"button\">View Profile</a>
              </p>
              <p>
                If you have any questions or need further information, please contact your system administrator.
              </p>
              <p>Best regards,<br />The CITRUS Team</p>
              <div class=\"footer\">
                ©CITRUS. All rights reserved.
              </div>
            </div>
          </body>
        </html>
        """.formatted(managerName, userName, updateDate, profileLink);
  }



  /**
   * Email template for notifying a user when their manager assigns a new course (English).
   */
  public static String COURSE_ASSIGNMENT_NOTIFICATION_TEMPLATE_EN(String userName, String managerName, String courseName, String assignDate, String courseLink) {
    return """
        <!DOCTYPE html>
        <html lang=\"en\">
          <head>
            <meta charset=\"UTF-8\" />
            <title>New Course Assigned</title>
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
            <div class=\"email-container\">
              <h2>New Course Assigned</h2>
              <p>Hello %s,</p>
              <p>
                Your manager <strong>%s</strong> has added a new course to you trainings: <strong>%s</strong> on %s.
              </p>
              <p>
                You can check it out using the link below:
              </p>
              <p style=\"text-align: center;\">
                <a href=\"%s\" class=\"button\">View Course</a>
              </p>
              <p>
                If you have any questions, please contact your manager or system administrator.
              </p>
              <p>Best regards,<br />The CITRUS Team</p>
              <div class=\"footer\">
                ©CITRUS. All rights reserved.
              </div>
            </div>
          </body>
        </html>
        """.formatted(userName, managerName, courseName, assignDate, courseLink);
  }

    /**
   * Email template for notifying a user that another user is trying to chat with them (English).
   */
  public static String CHAT_REQUEST_NOTIFICATION_TEMPLATE_EN(String recipientName, String senderName, String chatLink) {
    return """
        <!DOCTYPE html>
        <html lang=\"en\">
          <head>
            <meta charset=\"UTF-8\" />
            <title>New Chat Request</title>
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
            <div class=\"email-container\">
              <h2> New Chat Request</h2>
              <p>Hello %s,</p>
              <p>
                <strong>%s</strong> wants to start a chat with you.
              </p>
              <p style=\"text-align: center;\">
                <a href=\"%s\" class=\"button\">Open Chat</a>
              </p>
              <p>
                If you have any questions or concerns, please contact your system administrator.
              </p>
              <p>Best regards,<br />The CITRUS Team</p>
              <div class=\"footer\">
                ©CITRUS. All rights reserved.
              </div>
            </div>
          </body>
        </html>
        """.formatted(recipientName, senderName, chatLink);
  }

  }