// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => {
      if (key === "messageCenter.toMessageCenter") return "Ir para mensagens";
      return key;
    },
  }),
}));
jest.mock("../../stores/useNotificationStore", () => {
  const store = {
    messageNotifications: [
      {
        id: 1,
        notificationIsSeen: false,
        sender: { id: 42, name: "João" },
        message: "Nova mensagem",
      },
      {
        id: 2,
        notificationIsSeen: true,
        sender: { id: 43, name: "Maria" },
        message: "Outra mensagem",
      },
    ],
    setMessageUnreadCountToZero: jest.fn(),
    markMessageAsSeen: jest.fn(),
  };
  return (selector) => selector(store);
});
jest.mock("../../handles/handleNotificationApi", () => ({
  handleUpdateNotification: jest.fn(),
}));
jest.mock("react-router-dom", () => ({
  Link: ({ children, ...props }) => <a {...props}>{children}</a>,
  useNavigate: () => jest.fn(),
}));
jest.mock("../userIcon/UserIcon", () => () => (
  <div data-testid="user-icon-mock" />
));
// Mock NotificationItem para garantir que a mensagem aparece nos testes
jest.mock("./NotificationItem", () => (props) => (
  <div
    className="notification-item"
    onClick={() => props.onClick && props.onClick(props.notification)}
  >
    <div>{props.notification.message}</div>
  </div>
));

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import MessageDropdown from "./MessageDropdown";

describe("MessageDropdown", () => {
  const mockOnClose = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza lista de notificações", () => {
    render(<MessageDropdown isVisible={true} onClose={mockOnClose} />);
    expect(screen.getByText("Nova mensagem")).toBeInTheDocument();
    expect(screen.getByText("Outra mensagem")).toBeInTheDocument();
  });

  test("mostra mensagem de vazio quando não há notificações", () => {
    jest.resetModules();
    jest.doMock("../../stores/useNotificationStore", () => {
      const store = {
        messageNotifications: [],
        setMessageUnreadCountToZero: jest.fn(),
        markMessageAsSeen: jest.fn(),
      };
      return (selector) => selector(store);
    });
    // Importa o componente depois do mock
    const MessageDropdownReloaded = require("./MessageDropdown").default;
    render(<MessageDropdownReloaded isVisible={true} onClose={mockOnClose} />);
    expect(screen.getByText("No conversations found")).toBeInTheDocument();
    jest.resetModules(); // Limpa para os próximos testes
  });

  test("chama onClose ao clicar no botão de mensagens", () => {
    render(<MessageDropdown isVisible={true} onClose={mockOnClose} />);
    fireEvent.click(screen.getByText("Ir para mensagens"));
    expect(mockOnClose).toHaveBeenCalled();
  });

  test("chama handleNotificationClick ao clicar numa notificação", () => {
    render(<MessageDropdown isVisible={true} onClose={mockOnClose} />);
    fireEvent.click(screen.getByText("Nova mensagem"));
    expect(mockOnClose).toHaveBeenCalled();
  });
});
