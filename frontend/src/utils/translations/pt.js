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
  loginErrorAuthenticationCodeMissing: "O código 2FA é necessário",
  loginErrorAuthenticationCodeInvalid: "Código 2FA inválido",
  passwordResetRequestFailure:
    "Falha ao enviar o email de recuperação de palavra-passe. Por favor, tente novamente mais tarde.",
  passwordResetRequestSuccess:
    "Um email foi enviado para redefinir a sua palavra-passe.",

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
  forgotPasswordEmailSent:
    "Um email de recuperação de password foi enviado o seu email.",
  forgotPasswordEmailMissing: "O email é necessário",
  forgotPasswordEmailInvalid: "Formato de email inválido",

  //Two-Factor Authentication
  twoFactorTitle: "Configuração da 2FA",
  twoFactorWhatIsTitle: "O que é Autenticação de Dois Fatores (2FA)?",
  twoFactorWhatIsDescription:
    "A autenticação de dois fatores adiciona uma camada extra de segurança à sua conta. Após inserir sua senha, você precisará digitar um código de 6 dígitos gerado por um aplicativo autenticador no seu telefone.",
  twoFactorSetupTitle: "Como Configurar a Autenticação 2FA",
  twoFactorSetupStep1:
    "Abra o Google Authenticator, Microsoft Authenticator ou Authy.",
  twoFactorSetupStep2:
    "Toque em 'Adicionar uma nova conta' e escolha 'Entrada Manual'.",
  twoFactorSetupStep3: "Digite a chave secreta abaixo:",
  twoFactorSetupStep4:
    "Salve a configuração e o aplicativo começará a gerar códigos de 6 dígitos a cada 30 segundos.",
  twoFactorSecretPlaceholder: "CHAVE-SECRETA",
  twoFactorLoginTitle: "Entrando com 2FA",
  twoFactorLoginDescription:
    "Sempre que fizer login, insira o código de 6 dígitos mais recente mostrado no seu aplicativo autenticador. O código muda a cada 30 segundos, então certifique-se de usar o mais recente.",
  twoFactorCloseButton: "Entendi!",
  twoFactorRequest: "Solicitar código",
  twofactorErrorEmailMissing: "O email é necessário",
  twofactorErrorEmailInvalid: "Formato de email inválido",

  //Password Reset
  passwordResetLogo: "CITRUS",
  passwordResetTitle: "Redefinir sua senha",
  passwordResetSubtitle: "Digite sua nova senha",
  passwordResetFieldPassword: "Nova senha",
  passwordResetSubmit: "Redefinir senha",
  passwordResetFieldConfirmPassword: "Confirmar nova senha",
  passwordResetShowPassword: "Mostrar senha",
  passwordResetHidePassword: "Esconder senha",
  passwordResetErrorPasswordMissing: "A senha é necessária",
  passwordResetErrorConfirmPasswordMissing:
    "A confirmação da senha é necessária",
  passwordResetErrorPasswordMismatch: "As senhas não correspondem",
  passwordResetErrorPasswordWeak:
    "A senha deve ter pelo menos 12 caracteres e incluir letras maiúsculas, minúsculas, números e caracteres especiais.",
  passwordResetSuccess: "Password redefinida. Pode entrar com a nova password.",
  passwordResetError:
    "Erro ao redefinir a senha. Por favor, tente novamente mais tarde.",

  //ERRORS
  protectedRoute: "Tem de iniciar sessão para aceder a esta página",
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
  errorNetworkError:
    "Erro de rede. Verifica a tua conexão ou tenta mais tarde.",
  errorUnexpected:
    "Ocorreu um erro inesperado. Por favor, tenta novamente mais tarde",
  errorInvalidCredentials: "Email or password estão erradas",
  errorInvalidAuthCode: "Código de autenticação de dois fatores inválido",
  errorInvalidCodeRequest: "Email or password estão erradas",
  errorUserNotFound: "Utilizador não encontrado",
  errorTokenExpired: "O seu token expirou. Por favor, registe-se novamente.",
  errorExpiredPasswordResetToken:
    "O seu token de recuperação de password expirou. Por favor, solicite um novo.",

  // Validação
  errorPhoneNumberInvalid: "Formato de número de telefone inválido",
  errorEmailInvalid: "Formato de email inválido",

  // Not Found
  notfoundTitle: "404 Não Encontrado",
  notfoundPhrase: "Quando a vida te dá limões...",
  notfoundRedirect: "A redirecioná-lo dentro de {segundos} segundos.",

  // PROFILE
  profileUpdateSuccess: "Perfil atualizado com sucesso",
  profileUpdateError:
    "Erro ao atualizar o perfil. Por favor, tente novamente mais tarde.",
  profileFirstName: "Primeiro Nome",
  profileLastName: "Último Nome",
  profileBirthDate: "Data de Nascimento",
  profileRole: "Cargo",
  profileWorkplace: "Escritório",
  profilePhone: "Telefone",
  profileAddress: "Morada",
  profileAddressStreet: "Rua",
  profileAddressPostalCode: "Código Postal",
  profileAddressMunicipality: "Cidade",
  profileBiography: "Biografia",
  profilePlaceholderNA: "N/A",
  profileSave: "Guardar",
  profileEdit: "Editar",
  profileCancel: "Cancelar",

  // Profile Errors
  profileErrorFirstNameRequired: "O primeiro nome é obrigatório",
  profileErrorLastNameRequired: "O último nome é obrigatório",
  profileErrorBirthDateRequired: "A data de nascimento é obrigatória",
  profileErrorRoleRequired: "O cargo é obrigatório",
  profileErrorWorkplaceRequired: "O escritório é obrigatório",
  profileErrorPhoneRequired: "O telefone é obrigatório",
  profileErrorAddressRequired: "A morada é obrigatória",
  profileErrorAddressStreetRequired: "A rua é obrigatória",
  profileErrorAddressPostalCodeRequired: "O código postal é obrigatório",
  profileErrorAddressMunicipalityRequired: "A cidade é obrigatória",
  profileErrorBiographyRequired: "A biografia é obrigatória",

  // Profile Tabs
  profileTabProfile: "Perfil",
  profileTabTraining: "Formação",
  profileTabAppraisals: "Avaliações",
};

export default pt;
