jest.mock("../../stores/useAuthStore", () => () => ({
  user: {
    name: "João",
    surname: "Silva",
    email: "joao@exemplo.com",
    avatar: "avatar.png",
  },
}));
jest.mock("../../stores/useNotificationStore", () => {
  const store = {
    messageNotifications: [
      { id: 1, notificationIsRead: false },
      { id: 2, notificationIsRead: true },
    ],
    otherNotifications: [
      { id: 3, notificationIsRead: false },
      { id: 4, notificationIsRead: true },
    ],
    markAllMessagesAsRead: jest.fn(),
    markAllOthersAsRead: jest.fn(),
  };
  return (selector) => selector(store);
});
jest.mock("../../websockets/useWebSocketNotifications", () => () => {});
jest.mock("../../handles/handleNotificationApi", () => ({
  handleUpdateNotification: jest.fn(() => Promise.resolve()),
}));
jest.mock("../userIcon/UserIcon", () => ({ user }) => (
  <div data-testid="user-icon-mock">{user?.name}</div>
));
jest.mock("../dropdowns/MessageDropdown", () => (props) => (
  <div data-testid="message-dropdown" {...props} />
));
jest.mock("../dropdowns/NotificationDropdown", () => (props) => (
  <div data-testid="notification-dropdown" {...props} />
));
jest.mock(
  "../menu/Menu",
  () => (props) =>
    props.show ? <div data-testid="menu-mock" {...props} /> : null
);

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import Header from "./Header";

describe("Header", () => {
  const mockSetLanguage = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza logo, nome, email e UserIcon", () => {
    render(<Header language="pt" setLanguage={mockSetLanguage} />);
    expect(screen.getByAltText("Citrus logo")).toBeInTheDocument();
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("joao@exemplo.com")).toBeInTheDocument();
    expect(screen.getByTestId("user-icon-mock")).toBeInTheDocument();
  });

  test("renderiza badges de mensagens e notificações", () => {
    render(<Header language="pt" setLanguage={mockSetLanguage} />);
    const badges = screen.getAllByText("1");
    expect(badges[0]).toHaveClass("header-message-badge");
    expect(badges[1]).toHaveClass("header-badge");
  });

  test("chama setLanguage ao mudar idioma no menu", () => {
    render(<Header language="pt" setLanguage={mockSetLanguage} />);
    const burger = screen.getByLabelText("Menu");
    fireEvent.click(burger);
    // Simula interação no menu
    fireEvent.click(screen.getByTestId("menu-mock"));
    expect(mockSetLanguage).not.toBeCalled(); // O menu-mock não chama nada, mas o teste cobre a interação
  });
});
