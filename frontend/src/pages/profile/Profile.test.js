import { render, screen } from "@testing-library/react";
import Profile from "./Profile";
import { MemoryRouter } from "react-router-dom";

// Mock dependências externas
jest.mock("../../hooks/useUserProfile", () => {
  return () => ({
    user: {
      id: "1",
      name: "João",
      surname: "Silva",
      role: "ADMIN",
      office: "LISBOA",
      phone: "912345678",
      street: "Rua Exemplo",
      postalCode: "1234-567",
      municipality: "Lisboa",
      biography: "Exemplo de biografia",
      status: "active",
      manager: {
        id: "2",
        name: "Maria",
        surname: "Costa",
        role: "MANAGER",
        avatar: "",
        email: "maria@exemplo.com",
      },
      completedCourses: [],
    },
    userAvatar: null,
    managerAvatar: null,
    loading: false,
    refreshUser: jest.fn(),
  });
});

jest.mock("../../stores/useAuthStore", () => () => ({
  setUserAndExpiration: jest.fn(),
  setAvatar: jest.fn(),
  user: { id: "1", userIsAdmin: true },
}));

jest.mock("../../handles/handleUpdateUser", () => ({
  handleUpdateUserInfo: jest.fn(),
}));
jest.mock("../../handles/handleNotification", () => jest.fn());
jest.mock("../../utils/normalizeUserCourses", () => ({
  normalizeUserCourses: () => [],
}));
jest.mock("../../components/userIcon/UserIcon", () => () => (
  <div>UserIcon</div>
));
jest.mock("../../components/spinner/Spinner", () => () => <div>Spinner</div>);
jest.mock("./AppraisalsTab", () => () => <div>AppraisalsTab</div>);
jest.mock("./TrainingTab", () => () => <div>TrainingTab</div>);
jest.mock("../../components/userOffcanvas/UserOffcanvas", () => ({
  generateInitialsAvatar: () => "avatar-url",
}));

jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));

describe("Profile", () => {
  test("renderiza o título do perfil e os campos principais", () => {
    render(
      <MemoryRouter>
        <Profile />
      </MemoryRouter>
    );
    expect(screen.getByText(/profileOf/)).toBeInTheDocument();
    expect(screen.getByText(/João Silva/)).toBeInTheDocument();
    expect(screen.getByText(/profileTabProfile/)).toBeInTheDocument();
    expect(screen.getByText(/profileTabAppraisals/)).toBeInTheDocument();
    expect(screen.getByText(/profileTabTraining/)).toBeInTheDocument();
    expect(screen.getByText(/UserIcon/)).toBeInTheDocument();
  });

  test("mostra spinner quando loading é true", () => {
    // Mock loading true
    jest.doMock("../../hooks/useUserProfile", () => {
      return () => ({
        user: null,
        userAvatar: null,
        managerAvatar: null,
        loading: true,
        refreshUser: jest.fn(),
      });
    });
    // Precisas de limpar o cache do módulo para o novo mock ser usado
    jest.resetModules();
    const ProfileWithLoading = require("./Profile").default;
    render(
      <MemoryRouter>
        <ProfileWithLoading />
      </MemoryRouter>
    );
    expect(screen.getByText(/Spinner/)).toBeInTheDocument();
  });
});
