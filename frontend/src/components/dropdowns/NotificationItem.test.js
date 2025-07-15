// Mocks primeiro!
jest.mock("../userIcon/UserIcon", () => () => (
  <div data-testid="user-icon-mock" />
));
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, opts) => {
      if (key === "messageCenter.typeMessage") return "Mensagem";
      if (key === "notifications.appraisal") return "Nova avaliação";
      if (key === "notifications.cycleOpen") return "Ciclo aberto";
      if (key === "notifications.cycleClose") return "Ciclo fechado";
      if (key === "notifications.course") return "Novo curso";
      if (key === "notifications.userUpdate") return "Utilizador atualizado";
      if (key === "Notification") return "Notificação";
      return key;
    },
  }),
}));
jest.mock("../../utils/utilityFunctions", () => ({
  formatMessageTimestamp: () => "15/07/2025",
}));

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import NotificationItem from "./NotificationItem";

describe("NotificationItem", () => {
  const baseNotification = {
    id: 1,
    notificationIsSeen: false,
    sender: { id: 42, name: "João", surname: "Silva" },
    recipient: { id: 99, name: "Destinatário" },
    messageCount: 2,
    content: "Conteúdo",
    timestamp: "2025-07-15T10:00:00Z",
  };

  test("renderiza mensagem e sender para tipo MESSAGE", () => {
    render(
      <NotificationItem
        notification={{ ...baseNotification, type: "MESSAGE" }}
        onClick={jest.fn()}
      />
    );
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Conteúdo")).toBeInTheDocument(); // Corrigido!
    expect(screen.getByText("15/07/2025")).toBeInTheDocument();
    expect(screen.getByTestId("user-icon-mock")).toBeInTheDocument();
  });

  test("renderiza mensagem para tipo APPRAISAL", () => {
    render(
      <NotificationItem
        notification={{ ...baseNotification, type: "APPRAISAL" }}
        onClick={jest.fn()}
      />
    );
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Nova avaliação")).toBeInTheDocument();
  });

  test("renderiza mensagem para tipo CYCLE_OPEN", () => {
    render(
      <NotificationItem
        notification={{ ...baseNotification, type: "CYCLE_OPEN" }}
        onClick={jest.fn()}
      />
    );
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Ciclo aberto")).toBeInTheDocument();
  });

  test("renderiza mensagem para tipo CYCLE_CLOSE", () => {
    render(
      <NotificationItem
        notification={{ ...baseNotification, type: "CYCLE_CLOSE" }}
        onClick={jest.fn()}
      />
    );
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Ciclo fechado")).toBeInTheDocument();
  });

  test("renderiza mensagem para tipo COURSE", () => {
    render(
      <NotificationItem
        notification={{ ...baseNotification, type: "COURSE" }}
        onClick={jest.fn()}
      />
    );
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Novo curso")).toBeInTheDocument();
  });

  test("renderiza mensagem para tipo USER_UPDATE", () => {
    render(
      <NotificationItem
        notification={{ ...baseNotification, type: "USER_UPDATE" }}
        onClick={jest.fn()}
      />
    );
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Utilizador atualizado")).toBeInTheDocument();
  });

  test("renderiza mensagem default para tipo desconhecido", () => {
    render(
      <NotificationItem
        notification={{
          ...baseNotification,
          type: "UNKNOWN",
          content: undefined, // Corrigido: força fallback para "Notificação"
        }}
        onClick={jest.fn()}
      />
    );
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Notificação")).toBeInTheDocument();
  });

  test("chama onClick ao clicar na notificação", () => {
    const handleClick = jest.fn();
    render(
      <NotificationItem
        notification={{ ...baseNotification, type: "MESSAGE" }}
        onClick={handleClick}
      />
    );
    fireEvent.click(screen.getByText("Conteúdo")); // Corrigido!
    expect(handleClick).toHaveBeenCalledWith({
      ...baseNotification,
      type: "MESSAGE",
    });
  });

  test("aplica classe notification-seen quando notificationIsSeen é true", () => {
    const { container } = render(
      <NotificationItem
        notification={{
          ...baseNotification,
          type: "MESSAGE",
          notificationIsSeen: true,
        }}
        onClick={jest.fn()}
      />
    );
    expect(container.firstChild).toHaveClass("notification-seen");
  });
});
