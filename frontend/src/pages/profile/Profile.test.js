// Mocks primeiro!
jest.mock("../../i18n", () => ({
  default: {
    t: function (key) {
      return key;
    },
  },
}));

jest.mock("../../handles/handleNotification", () => ({
  __esModule: true,
  default: jest.fn(),
}));

jest.mock("react-hook-form", () => ({
  useForm: () => ({
    register: jest.fn(),
    handleSubmit: jest.fn(),
    reset: jest.fn(),
    setValue: jest.fn(),
    getValues: jest.fn(),
    formState: { errors: {} },
    control: {},
    watch: jest.fn(),
  }),
}));

jest.mock("../../api/api", () => ({
  api: {
    get: jest.fn(() => Promise.resolve({ data: [] })),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
    interceptors: {
      request: { use: jest.fn() },
      response: { use: jest.fn() },
    },
  },
}));

let mockLoading = false;

jest.mock("../../hooks/useUserProfile", () => {
  return () => ({
    user: mockLoading
      ? null
      : {
          id: "1",
          name: "João",
          surname: "Silva",
          role: "ADMIN",
          office: "LISBOA",
          status: "active",
          manager: null,
          completedCourses: [],
        },
    userAvatar: null,
    managerAvatar: null,
    loading: mockLoading,
    refreshUser: jest.fn(),
  });
});

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));

jest.mock("../../components/userIcon/UserIcon", () => () => (
  <div>UserIcon</div>
));
jest.mock("../../components/spinner/Spinner", () => () => <div>Spinner</div>);
jest.mock("./AppraisalsTab", () => () => <div>AppraisalsTab</div>);
jest.mock("./TrainingTab", () => () => <div>TrainingTab</div>);
jest.mock("../../i18n", () => ({
  default: {
    t: (key) => key,
  },
}));

// Só depois importas os módulos!
import { render, screen, cleanup } from "@testing-library/react";
import Profile from "./Profile";
import { MemoryRouter } from "react-router-dom";

afterEach(cleanup);

describe("Profile", () => {
  test("renderiza o componente Profile sem falhar", () => {
    mockLoading = false;
    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );
    expect(screen.getByText(/profileOf/)).toBeInTheDocument();
    expect(screen.getAllByText(/João Silva/).length).toBeGreaterThan(0);
    expect(screen.getByText(/UserIcon/)).toBeInTheDocument();
  });

  test("mostra spinner quando loading é true", () => {
    mockLoading = true;
    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );
    expect(screen.getByText(/Spinner/)).toBeInTheDocument();
  });

  test("o botão de editar aparece", () => {
    mockLoading = false;
    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );
    expect(screen.getByText(/profileEdit/)).toBeInTheDocument();
  });

  test("o nome do utilizador aparece no modo de edição", async () => {
    mockLoading = false;
    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );
    // Simula clicar no botão de editar
    const editButton = screen.getByText(/profileEdit/);
    editButton.click();

    // Espera que os inputs fiquem editáveis (disabled=false)
    const nomeInput = await screen.findByRole("textbox", {
      name: /profileFirstName/i,
    });
    const apelidoInput = await screen.findByRole("textbox", {
      name: /profileLastName/i,
    });

    // Verifica se os campos de nome e apelido aparecem
    expect(nomeInput).toBeInTheDocument();
    expect(apelidoInput).toBeInTheDocument();
  });

  test("o campo de role mostra o valor correto", () => {
    mockLoading = false;
    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );
    // Verifica se o input do role tem o valor "ADMIN"
    expect(screen.getByDisplayValue("ADMIN")).toBeInTheDocument();
  });

  test("o campo workplace mostra o valor correto", () => {
    mockLoading = false;
    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );
    // Verifica se o input do workplace tem o valor "LISBOA"
    expect(screen.getByDisplayValue("LISBOA")).toBeInTheDocument();
  });

  test('mostra "No manager assigned" quando não há manager', () => {
    mockLoading = false;
    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );
    expect(screen.getByText(/No manager assigned/)).toBeInTheDocument();
  });

  test("os tabs aparecem", () => {
    mockLoading = false;
    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );
    expect(screen.getByText(/profileTabProfile/)).toBeInTheDocument();
    expect(screen.getByText(/profileTabAppraisals/)).toBeInTheDocument();
    expect(screen.getByText(/profileTabTraining/)).toBeInTheDocument();
  });
});
