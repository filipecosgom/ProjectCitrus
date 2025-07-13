import { courseSearchFilters } from "../coursesSearchUtils";

const pt = {
  notifications: {
    appraisal: "Teve {content} numa nova avaliação",
    cycle: "Novo ciclo iniciado até {content}",
    course: "Registou o curso {content}",
  },
  welcomeMessage: "Bem-vindo {name}",
  goodByeMessage: "Até à próxima {name}",
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

  //Profile
  profileUserUpdated: "Utilizador atualizado com sucesso",

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

  // Profile Invalid Characters
  profileErrorFirstNameInvalid: "O primeiro nome contém caracteres inválidos",
  profileErrorLastNameInvalid: "O último nome contém caracteres inválidos",
  profileErrorBirthDateFuture: "A data de nascimento não pode ser futura",
  profileErrorBirthDateTooYoung: "Deve ter pelo menos 16 anos",
  errorPostalCodeInvalid: "Formato de código postal inválido (ex: 1234-567)",
  profileErrorAddressStreetInvalid: "A rua contém caracteres inválidos",
  profileErrorAddressMunicipalityInvalid:
    "A cidade contém caracteres inválidos",

  // Profile Tabs
  profileTabProfile: "Perfil",
  profileTabTraining: "Formação",
  profileTabAppraisals: "Avaliações",

  //Users
  users: {
    assignManagers: "Atribuir Gestores",
    assignManagerTitle:
      "Atribuir Gestor a {count, plural, one {# utilizador} other {# utilizadores}}",
    willManage:
      "Irá gerir {count, plural, one {# utilizador} other {# utilizadores}}",
    selectedUsers: "Utilizadores Selecionados:",
    selectUserToPromote: "Selecionar Utilizador para Promover a Gestor:",
    promoteDescription:
      "Procure um utilizador que será promovido a gestor e atribuído aos utilizadores selecionados acima.",
    searchPromotePlaceholder: "Procurar utilizador para promover a gestor...",
    willBePromoted: "será promovido a Gestor",
    cancel: "Cancelar",
    assigning: "A atribuir...",
    promoteAndAssign: "Promover & Atribuir",
    selectUserFirst: "Selecione primeiro o utilizador",
    noResults:
      "Nenhum utilizador encontrado que corresponda aos seus critérios",
    na: "N/A",
    accountStateComplete: "Completo",
    accountStateIncomplete: "Incompleto",
    avatarLoading: "A carregar...",
    viewProfile: "Ver Perfil",
    noUsersSelected: "Nenhum utilizador selecionado",
    managerAssignSuccess:
      "{managerName} agora é o gestor de {total} utilizador(es)",
  },

  //Usercard
  userCardNoManager: "Sem gestor",
  //Appraisal
  appraisalsNoResults:
    "Nenhuma avaliação encontrada que corresponda aos seus critérios",
  //AppraisalCard
  appraisalStateCompleted: "Completo",
  appraisalStateInProgress: "Em curso",
  appraisalStateClosed: "Fechado",
  appraisalsPdfExportError:
    "Falha ao exportar avaliações para PDF. Por favor, tente novamente mais tarde.",

  appraisalSortControlsUser: "Utilizador",
  appraisalSortControlsScore: "Score",
  appraisalSortControlsManager: "Manager",
  appraisalSortControlsEndDate: "Data de fim",
  appraisalSortControlsState: "Estado",

  //SearchBar
  searchBarSearchButton: "Pesquisar",
  searchBarPlaceholder: "Pesquisar por {type}…",
  searchBarAllStates: "Todos os estados",
  searchBarComplete: "Completo",
  searchBarIncomplete: "Incompleto",
  searchBarLimitToggle: "Resultados por página",
  searchByEmail: "email",
  searchByName: "nome",
  searchByRole: "cargo",

  //Usercontrols
  userSortControlsName: "Nome",
  userSortControlsRole: "Cargo",
  userSortControlsOffice: "Escritório",
  userSortControlsManager: "Gestor",
  userSortControlsSortAscending: "Ordenar de A a Z",
  userSortControlsSortDescending: "Ordenar de Z a A",

  //Fltermenu
  filterMenuSettings: "Filtros",
  filterMenuSearchType: "Tipo de pesquisa",
  filterMenuOptionName: "Nome",
  filterMenuOptionEmail: "E-mail",
  filterMenuOptionRole: "Cargo",
  filterMenuOffice: "Escritório",
  filterMenuState: "Estado",
  filterMenuAllOffices: "Todos os escritórios",
  filterMenuAccountState: "Estado da conta",
  filterMenuOptionComplete: "Completo",
  filterMenuOptionIncomplete: "Incompleto",
  filterMenuisManager: "Apenas gestores",
  filterMenuisAdmin: "Apenas administradores",
  filterMenuisManaged: "Utilizadores com gestores",
  filterYes: "Sim",
  filterNo: "Não",

  // Menu internationalization
  menuDashboard: "Painel",
  menuProfile: "Perfil",
  menuUsers: "Utilizadores",
  menuTraining: "Formação",
  menuAppraisal: "Avaliações",
  menuCycles: "Ciclos",
  menuSettings: "Definições",
  menuDarkMode: "Escuro",
  menuDarkModeBeta: "Beta",
  menuLanguage: "Idioma",
  menuLogout: "Sair",

  messageCenter: {
    searchPlaceholder: "Procurar utilizador para iniciar conversa...",
    lastOnlineAt: "Última vez online às",
    statusSending: "⏳",
    statusFailed: "❌",
    statusSent: "✓",
    statusRead: "✓✓",
    statusNotRead: "✓✓",
    typeMessage: "Escreva uma mensagem...",
    selectConversation: "Selecione uma conversa para começar a conversar",
    toMessageCenter: "Ir para Central de Mensagens",
  },

  // Avaliações
  appraisalsSearchTypeUserName: "nome",
  appraisalsSearchTypeUserEmail: "email",
  appraisalsSearchTypeManagerName: "nome do gestor",
  appraisalsSearchTypeManagerEmail: "email do gestor",
  appraisalStateAllStates: "Todos os estados",

  appraisal: {
    scoreVerbose: {
      null: "Nenhuma pontuação atribuída",
      1: "Contribuição baixa",
      2: "Contribuição parcial",
      3: "Contribuição conforme o esperado",
      4: "Contribuição excedida",
    },
    submit: "Submeter",
    save: "Guardar",
    saved: "Avaliação guardada. Pode submeter até {endDate}",
    saving: "Salvando...",
    submitted: "Avaliação submetida com sucesso.",
    cancel: "Cancelar",
    edit: "Editar",
    feedback: "Feedback",
    feedbackText: "Texto do feedback",
    score: "Pontuação",
    noFeedback: "Nenhum feedback fornecido.",
    saveError: "Erro ao salvar feedback.",
    error: "Falha ao guardar avaliação.",
    noAppraisalsSelected: "Nenhuma avaliação selecionada",
    completeAppraisals: "Completar",
    pdfGenerated: "PDF gerado com sucesso. Pode descarregá-lo agora.",
    pdfGenerationFailed:
      "Falha ao gerar PDF. Por favor, tente novamente mais tarde.",
  },

  // Ciclos
  cycles: {
    title: "Gestão de Ciclos",
    createNewCycle: "Criar Novo Ciclo",
    startDate: "Data de Início",
    endDate: "Data de Fim",
    appraisals: "Avaliações",
    statusOpen: "Aberto",
    statusClosed: "Fechado",
    cycleTitle: "Ciclo {id}",
    closeCycle: "Fechar Ciclo",
    confirmCloseCycleTitle: "Fechar Ciclo",
    confirmCloseCycle:
      "Tem certeza que deseja fechar este ciclo? Esta ação não pode ser desfeita.",
    cycleClosedSuccess:
      "Ciclo {id} fechado com sucesso! ({startDate} - {endDate})",
    errorClosingCycle: "Erro ao fechar ciclo",
    errorLoadingCycles: "Erro ao carregar ciclos",
    loading: "A carregar ciclos...",
    tryAgain: "Tentar novamente",
    noCyclesFound: "Nenhum ciclo encontrado.",
    previous: "Anterior",
    next: "Próximo",
    pageInfo: "Página {current} de {total}",
    na: "N/D",
    appraisalsToComplete: "{count} avaliações para completar",
    daysDuration: "{days} dias",
    createCycle: "Criar Ciclo",
    creating: "A criar...",

    // ✅ CORRIGIR: Remover duplicações e manter apenas estas versões:
    errorCreateCycle: "Falha ao criar ciclo. Tente novamente.",
    errorInvalidDateRange: "Por favor, selecione um intervalo de datas válido.",

    // Novas traduções para o CycleDetailsOffcanvas
    dateRange: "Período de Datas",
    summary: "Resumo",
    duration: "Duração",
    status: "Estado",
    appraisalsList: "Lista de Avaliações",
    statusCompleted: "Concluído",
    statusPending: "Pendente",
    statusInProgress: "Em Progresso",

    // Correção da tradução existente
    cycleClosedSuccessToast:
      "Ciclo {id} fechado com sucesso! ({startDate} - {endDate})",

    // ✅ ADICIONAR: Traduções para notificações de email
    cycleCreated: "Ciclo criado com sucesso!",
    cycleCreatedEmailWarning:
      "Ciclo criado, mas algumas notificações por email falharam.",

    // Restantes traduções...
    userId: "Utilizador",
    cannotCloseCycle: "Não é possível fechar o ciclo",
    appraisalsNotClosed: "Existem avaliações que não estão fechadas",
    understood: "Compreendido",
    appraisalsSummary: "Resumo das Avaliações",
    totalAppraisals: "Total de Avaliações",
    pendingAppraisals: "Avaliações Pendentes",
    appraisalsBreakdown: "Detalhamento:",
    inProgressAppraisals:
      "{count, plural, one {# avaliação em progresso} other {# avaliações em progresso}}",
    completedAppraisals:
      "{count, plural, one {# avaliação concluída} other {# avaliações concluídas}}",
    closeCycleInstruction:
      "Para fechar este ciclo, todas as avaliações devem estar no estado 'Fechado'.",
    appraisalsNotCompleted:
      "Não é possível fechar este ciclo porque existem avaliações que ainda não foram completadas.",
    closeCycleInstructionCompleted:
      "Para fechar este ciclo, todas as avaliações devem estar no estado 'Completado'.",
    checkPendingAppraisals: "Verificar Avaliações Pendentes",
  },

  courses: {
    title: "Cursos",
    searchPlaceholder: "Pesquisar cursos...",
    noResults: "Nenhum curso encontrado.",
    loading: "A carregar cursos...",
    sortTitle: "Título",
    sortArea: "Área",
    sortDuration: "Duração",
    sortLanguage: "Idioma",
    searchByTitle: "Título",
    searchByDescription: "Descrição",
    searchByArea: "Área",
    filterAllAreas: "Todas as áreas",
    filterAllLanguages: "Todos os idiomas",
    filterPortuguese: "Português",
    filterEnglish: "Inglês",
    details: "Detalhes do Curso",
    instructor: "Instrutor",
    duration: {
      label: "Duração",
      hours: "{hours}h",
      minutes: "{minutes}min",
    },
    language: "Idioma",
    area: "Área",
    createdDate: "Data de Criação",
    completionDate: "Data de Conclusão",
    description: "Descrição",
    startCourse: "Abrir",
    viewCourse: "Ver Curso",
    addNewCourse: "Adicionar Curso",
    newTitle: "Título",
    newDescription: "Descrição",
    selectArea: "Selecionar Área",
    selectLanguage: "Selecionar Idioma",
    link: "Link",
    courseCreated: "Curso criado com sucesso",
    inactive: "Inativo",
    editCourse: "Editar",
    inactivateCourse: "Inativar",
    activateCourse: "Ativar",
    courseUpdateSuccess: "Curso atualizado com sucesso",
    errorTitleRequired: "O título é obrigatório",
    errorAreaRequired: "A área é obrigatória",
    errorLanguageRequired: "O idioma é obrigatório",
    errorDescriptionRequired: "A descrição é obrigatória",
    errorLinkRequired: "O link é obrigatório",
    errorLinkInvalid: "URL inválido",
    errorCourseNotCreated: "Curso não foi criado (título ou link duplicado)",
    confirmInactivate: "Tem certeza que deseja inativar este curso?",
    confirmActivate: "Tem certeza que deseja ativar este curso?",
    clearFilters: "Limpar filtros",
    totalHours: "Horas totais",
    allYears: "Todos os anos",
    // Adições para AddCompletedCourseOffcanvas e CourseSearchBar
    addCompletedCourse: "Adicionar cursos",
    addCompletedCourseTitle: "Registar novos cursos a {userName} {userSurname}",
    selectCourse: "Selecionar curso",
    adding: "Adicionando...",
    completedCourseAdded: "Curso(s) concluído(s) adicionado(s) com sucesso!",
    errorCompletedCourseNotAdded: "Não foi possível adicionar o curso concluído.",
    errorAlreadyCompleted: "Este curso já está marcado como concluído.",
    errorUserOrCourseNotFound: "Usuário ou curso não encontrado.",
    clear: "Limpar",
    searching: "Pesquisando...",
    noAvailable: "Nenhum curso disponível.",
    cancel: "Cancelar",
    remove: "Remover",
  },

  common: {
    confirm: "Confirmar",
    cancel: "Cancelar",
    confirmMessage: "Tem certeza que deseja prosseguir?",
  },

  // Adicionar estas traduções para o FilterMenu funcionar:
  filterMenuCategory: "Categoria",
  filterMenuLanguage: "Idioma",

  login: {
    success: "Login efetuado com sucesso!",
    avatarError: "Login efetuado, mas falha ao carregar o avatar.",
    failed: "Falha no login. Verifique as credenciais e tente novamente.",
  },

  
};

export default pt;
