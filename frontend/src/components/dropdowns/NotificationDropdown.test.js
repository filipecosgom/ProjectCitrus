// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => {
      if (key === "TO NOTIFICATION CENTER") return "Ir para notificações";
      if (key === "No notifications") return "Sem notificações";
      return key;
    },
  }),
}));
jest.mock("../../stores/useNotificationStore", () => {
  const store = {
    otherNotifications: [
      {
        id: 1,
        notificationIsSeen: false,
        sender: { id: 42, name: "João" },
        recipient: { id: 99, name: "Destinatário" },
        type: "APPRAISAL",
        message: "Nova notificação",
      },
      {
        id: 2,
        notificationIsSeen: true,
        sender: { id: 43, name: "Maria" },
        recipient: { id: 100, name: "Outro Destinatário" },
        type: "CYCLE_OPEN",
        message: "Outra notificação",
      },
    ],
    markOtherAsSeen: jest.fn(),
  };
  return (selector) => selector(store);
});
jest.mock("../../stores/useAuthStore", () => () => ({
  user: { id: 123, name: "Utilizador" },
}));
jest.mock("../../handles/handleNotificationApi", () => ({
  handleUpdateNotification: jest.fn(),
}));
jest.mock("react-router-dom", () => ({
  Link: ({ children, ...props }) => <a {...props}>{children}</a>,
  useNavigate: () => jest.fn(),
}));
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
import NotificationDropdown from "./NotificationDropdown";

describe("NotificationDropdown", () => {
  const mockOnClose = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza lista de notificações", () => {
    render(<NotificationDropdown isVisible={true} onClose={mockOnClose} />);
    expect(screen.getByText("Nova notificação")).toBeInTheDocument();
    expect(screen.getByText("Outra notificação")).toBeInTheDocument();
  });

  test("mostra mensagem de vazio quando não há notificações", () => {
    jest.resetModules();
    jest.doMock("../../stores/useNotificationStore", () => {
      const store = {
        otherNotifications: [],
        markOtherAsSeen: jest.fn(),
      };
      return (selector) => selector(store);
    });
    const NotificationDropdownReloaded =
      require("./NotificationDropdown").default;
    render(
      <NotificationDropdownReloaded isVisible={true} onClose={mockOnClose} />
    );
    expect(screen.getByText("Sem notificações")).toBeInTheDocument();
    jest.resetModules();
  });

  test("chama onClose ao clicar no botão de centro de notificações", () => {
    render(<NotificationDropdown isVisible={true} onClose={mockOnClose} />);
    fireEvent.click(screen.getByText("Ir para notificações"));
    expect(mockOnClose).toHaveBeenCalled();
  });

  test("chama handleNotificationClick ao clicar numa notificação", () => {
    render(<NotificationDropdown isVisible={true} onClose={mockOnClose} />);
    fireEvent.click(screen.getByText("Nova notificação"));
    expect(mockOnClose).toHaveBeenCalled();
  });

  test("limita o número de notificações visíveis a 6", () => {
    jest.resetModules();
    jest.doMock("../../stores/useNotificationStore", () => {
      const store = {
        otherNotifications: Array.from({ length: 10 }, (_, i) => ({
          id: i + 1,
          notificationIsSeen: false,
          sender: { id: i, name: `Sender${i}` },
          recipient: { id: 200 + i, name: `Dest${i}` },
          type: "APPRAISAL",
          message: `Notificação ${i + 1}`,
        })),
        markOtherAsSeen: jest.fn(),
      };
      return (selector) => selector(store);
    });
    const NotificationDropdownReloaded =
      require("./NotificationDropdown").default;
    render(
      <NotificationDropdownReloaded isVisible={true} onClose={mockOnClose} />
    );
    for (let i = 1; i <= 6; i++) {
      expect(screen.getByText(`Notificação ${i}`)).toBeInTheDocument();
    }
    expect(screen.queryByText("Notificação 7")).not.toBeInTheDocument();
    jest.resetModules();
  });
});
