const pt = {
  //Login
  loginLogo: "CITRUS",
  loginTitle: "Entrar",
  loginSubtitle: "Aceda à sua conta",
  loginFieldEmail: "Endereço de e-mail",
  loginFieldPassword: "Palavra-passe",
  loginFieldTwoFAuth: "Autenticação de Dois Fatores",
  loginShowPassword: "Mostrar palavra-passe",
  loginHidePassword: "Esconder palavra-passe",
  loginForgotPassword: "Esqueceu-se da palavra-passe?",
  loginHelpTwoFAuth: "Precisa de ajuda com a autenticação de dois fatores?",
  loginSubmit: "Entrar",
  loginRegisterPrompt: "Ainda não tem uma conta?",
  loginRegister: "Junte-se à CITRUS hoje",

  loginErrorEmailMissing: "O email é necessário",
  loginErrorEmailInvalid: "Formato de email inválido",
  loginErrorPasswordMissing: "A palavra-passe é necessária",
  loginErrorAuthenticationCodeMissing: "O código de autenticação de dois fatores é necessário",
  loginErrorAuthenticationCodeInvalid: "Código de autenticação de dois fatores inválido",

  // Register
  registerTitle: "Bem-vindo à CITRUS",
  registerSubtitle: "Registre sua conta",
  registerFieldEmail: "Endereço de e-mail",
  registerFieldPassword: "Palavra-passe",
  registerFieldConfirmPassword: "Confirmar palavra-passe",
  registerShowPassword: "Mostrar palavra-passe",
  registerHidePassword: "Esconder palavra-passe",
  registerSubmit: "Criar conta",
  registerAlreadyAccount: "Já tem uma conta?",
  registerLogin: "Entrar",
  registerLogo: "Logotipo CITRUS",
  registerLogoContainerTitle: "Junte-se à comunidade CITRUS!",
  registerButton: "Voltar ao Login",

  registerErrorEmailMissing: "O email é necessário",
  registerErrorEmailInvalid: "Formato de email inválido",
  registerErrorPasswordMissing: "A palavra-passe é necessária",
  registerErrorConfirmPasswordMissing:
    "A confirmação da palavra-passe é necessária",
  registerErrorPasswordMismatch: "As palavras-passe não correspondem",
  registerErrorPasswordWeak:
    "A senha deve ter pelo menos 12 caracteres e incluir letras maiúsculas, minúsculas, números e caracteres especiais.",

  //Activation
  activationTitle: "Ativação da Conta",
  activationSuccessMessage: "Conta criada com sucesso!",
  activationEmailConfirmation:
    "A tua conta com o email {email} foi criada. " +
    "Enviámos um email de confirmação com um link para validares a tua conta. " +
    "Por favor verifica a tua caixa de entrada.",
  activationRedirectMessage:
    "Serás redirecionado para o login em {segundos} segundos.",
  activatedAccountTitle: "Conta ativada!",
  activatedAccountMessage:
    "A sua conta foi activada, pode efetuar login.<br />Bem-vindo ao Citrus.",
  activatedAccountRedirectMessage:
    "Serás redirecionado para o login em {segundos} segundos.",
  activatedAccountButton: "Voltar ao Login",

  // Forgot Password
  forgotPasswordTitle: "Redefinir palavra-passe",
  forgotPasswordSubtitle: "Escreva o seu endereço de e-mail",
  forgotPasswordFieldEmail: "Endereço de e-mail",
  forgotPasswordSubmit: "Redefinir palavra-passe",

  //Two-Factor Authentication
  twoFactorTitle: "Configuração da 2FA",
  twoFactorWhatIsTitle: "O que é Autenticação de Dois Fatores (2FA)?",
  twoFactorWhatIsDescription: "A autenticação de dois fatores adiciona uma camada extra de segurança à sua conta. Após inserir sua senha, você precisará digitar um código de 6 dígitos gerado por um aplicativo autenticador no seu telefone.",
  twoFactorSetupTitle: "Como Configurar a Autenticação 2FA",
  twoFactorSetupStep1: "Abra o Google Authenticator, Microsoft Authenticator ou Authy.",
  twoFactorSetupStep2: "Toque em 'Adicionar uma nova conta' e escolha 'Entrada Manual'.",
  twoFactorSetupStep3: "Digite a chave secreta abaixo:",
  twoFactorSetupStep4: "Salve a configuração e o aplicativo começará a gerar códigos de 6 dígitos a cada 30 segundos.",
  twoFactorSecretPlaceholder: "CHAVE-SECRETA",
  twoFactorLoginTitle: "Entrando com 2FA",
  twoFactorLoginDescription: "Sempre que fizer login, insira o código de 6 dígitos mais recente mostrado no seu aplicativo autenticador. O código muda a cada 30 segundos, então certifique-se de usar o mais recente.",
  twoFactorCloseButton: "Entendi!",
  twoFactorRequest: "Solicitar código",
  twofactorErrorEmailMissing: "O email é necessário",
  twofactorErrorEmailInvalid: "Formato de email inválido",

  //ERRORS
  // Erros Gerais
  infoAboutToExpire: "A sua sessão está prestes a expirar",
  infoSessionExpired: "A sua sessão expirou. Por favor, faça login novamente.",
  errorInvalidData: "Dados inválidos fornecidos.",
  errorWrongUsernamePassword: "Nome de usuário ou senha incorretos.",
  errorAccountInactive: "A tua conta está inativa.",
  errorAccountExcluded: "A tua conta foi excluída.",
  errorForbidden: "Não tens permissão para acessar este recurso.",
  errorDuplicateEntry: "Este email já está registado.",
  errorServerIssue: "Erro no servidor. Tenta novamente mais tarde.",
  errorFailed: "Falha no pedido. Tenta novamente mais tarde.",
  errorNetworkError: "Erro de rede. Verifica a tua conexão ou tenta mais tarde.",
  errorUnexpected: "Ocorreu um erro inesperado. Por favor, tenta novamente mais tarde",
  errorInvalidCredentials: "Email or password estão erradas",
  errorInvalidCodeRequest: "Email or password estão erradas",

  // Not Found
  notfoundTitle: "404 Não Encontrado",
  notfoundPhrase: "Quando a vida te dá limões...",
  notfoundRedirect: "A redirecioná-lo dentro de {segundos} segundos.",
};

export default pt;
