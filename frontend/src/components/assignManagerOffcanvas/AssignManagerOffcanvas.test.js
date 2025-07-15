// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, opts) => key,
  }),
}));
jest.mock("../userSearchBar/UserSearchBar", () => (props) => (
  <div data-testid="user-search-bar" />
));
jest.mock("../spinner/Spinner", () => () => <span data-testid="spinner" />);
jest.mock("../userIcon/UserIcon", () => (props) => (
  <span data-testid="user-icon">{props.user?.name}</span>
));
jest.mock("../../handles/handleNotification", () => jest.fn());

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import AssignManagerOffcanvas from "./AssignManagerOffcanvas";

describe("AssignManagerOffcanvas", () => {
  const selectedUsers = [
    {
      id: "1",
      name: "João",
      surname: "Silva",
      role: "EMPLOYEE",
      email: "joao@teste.com",
    },
    {
      id: "2",
      name: "Maria",
      surname: "Costa",
      role: "MANAGER",
      email: "maria@teste.com",
    },
  ];

  const defaultProps = {
    selectedUserIds: ["1", "2"],
    selectedUsers,
    isOpen: true,
    onClose: jest.fn(),
    onAssign: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("não renderiza nada se shouldRender for false", () => {
    const { container } = render(
      <AssignManagerOffcanvas {...defaultProps} isOpen={false} />
    );
    expect(container.firstChild).toBeNull();
  });

  test("renderiza título e lista de utilizadores", () => {
    render(<AssignManagerOffcanvas {...defaultProps} />);
    expect(screen.getByText("users.assignManagerTitle")).toBeInTheDocument();
    expect(screen.getByText("users.selectedUsers")).toBeInTheDocument();
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Maria Costa")).toBeInTheDocument();
  });

  test("renderiza barra de pesquisa de utilizadores", () => {
    render(<AssignManagerOffcanvas {...defaultProps} />);
    expect(screen.getByTestId("user-search-bar")).toBeInTheDocument();
  });

  test("renderiza botões de cancelar e atribuir", () => {
    render(<AssignManagerOffcanvas {...defaultProps} />);
    expect(screen.getByText("users.cancel")).toBeInTheDocument();
    expect(screen.getByText("users.selectUserFirst")).toBeInTheDocument();
  });

  test("chama onClose ao clicar no botão cancelar", () => {
    render(<AssignManagerOffcanvas {...defaultProps} />);
    fireEvent.click(screen.getByText("users.cancel"));
    expect(defaultProps.onClose).toHaveBeenCalledTimes(1);
  });

  test("não chama onAssign se não houver utilizadores selecionados", () => {
    render(<AssignManagerOffcanvas {...defaultProps} selectedUserIds={[]} />);
    // O botão tem o texto "users.selectUserFirst" quando está desativado
    fireEvent.click(screen.getByText("users.selectUserFirst"));
    expect(defaultProps.onAssign).toHaveBeenCalledTimes(0);
  });
});
