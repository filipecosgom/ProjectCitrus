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
import NotificationRow from "./NotificationRow";

const baseNotification = {
  id: 1,
  notificationIsSeen: false,
  notificationIsRead: false,
  sender: { id: 42, name: "João", surname: "Silva" },
  recipient: { id: 99, name: "Destinatário" },
  messageCount: 2,
  content: "Conteúdo",
  timestamp: "2025-07-15T10:00:00Z",
  type: "MESSAGE",
};

describe("NotificationRow", () => {
  test("renderiza mensagem e sender para tipo MESSAGE", () => {
    render(
      <NotificationRow
        notification={baseNotification}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Conteúdo")).toBeInTheDocument();
    expect(screen.getByText("15/07/2025")).toBeInTheDocument();
    expect(screen.getByTestId("user-icon-mock")).toBeInTheDocument();
  });

  test("renderiza mensagem traduzida para tipo APPRAISAL", () => {
    render(
      <NotificationRow
        notification={{ ...baseNotification, type: "APPRAISAL" }}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(screen.getByText("Nova avaliação")).toBeInTheDocument();
  });

  test("renderiza mensagem traduzida para tipo CYCLE_OPEN", () => {
    render(
      <NotificationRow
        notification={{ ...baseNotification, type: "CYCLE_OPEN" }}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(screen.getByText("Ciclo aberto")).toBeInTheDocument();
  });

  test("renderiza mensagem traduzida para tipo CYCLE_CLOSE", () => {
    render(
      <NotificationRow
        notification={{ ...baseNotification, type: "CYCLE_CLOSE" }}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(screen.getByText("Ciclo fechado")).toBeInTheDocument();
  });

  test("renderiza mensagem traduzida para tipo COURSE", () => {
    render(
      <NotificationRow
        notification={{ ...baseNotification, type: "COURSE" }}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(screen.getByText("Novo curso")).toBeInTheDocument();
  });

  test("renderiza mensagem traduzida para tipo USER_UPDATE", () => {
    render(
      <NotificationRow
        notification={{ ...baseNotification, type: "USER_UPDATE" }}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(screen.getByText("Utilizador atualizado")).toBeInTheDocument();
  });

  test("renderiza mensagem default para tipo desconhecido", () => {
    render(
      <NotificationRow
        notification={{
          ...baseNotification,
          type: "UNKNOWN",
          content: undefined,
        }}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(screen.getByText("Notificação")).toBeInTheDocument();
  });

  test("chama onClick ao clicar na notificação", () => {
    const handleClick = jest.fn();
    render(
      <NotificationRow
        notification={baseNotification}
        onClick={handleClick}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    fireEvent.click(screen.getByText("Conteúdo"));
    expect(handleClick).toHaveBeenCalledWith(baseNotification);
  });

  test("chama onSelectionChange ao clicar no checkbox", () => {
    const handleSelectionChange = jest.fn();
    render(
      <NotificationRow
        notification={baseNotification}
        onClick={jest.fn()}
        onSelectionChange={handleSelectionChange}
        isSelected={false}
      />
    );
    fireEvent.click(screen.getByRole("checkbox"));
    expect(handleSelectionChange).toHaveBeenCalledWith(
      baseNotification.id,
      true
    );
  });

  test("aplica classe notification-seen quando notificationIsSeen é true", () => {
    const { container } = render(
      <NotificationRow
        notification={{
          ...baseNotification,
          notificationIsSeen: true,
        }}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(container.firstChild).toHaveClass("notification-seen");
  });

  test("checkbox está selecionado quando isSelected é true", () => {
    render(
      <NotificationRow
        notification={baseNotification}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={true}
      />
    );
    expect(screen.getByRole("checkbox")).toBeChecked();
  });

  test("mensagem tem classe unread quando notificationIsRead é false", () => {
    const { container } = render(
      <NotificationRow
        notification={{ ...baseNotification, notificationIsRead: false }}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(container.querySelector(".notificationRow-message")).toHaveClass(
      "unread"
    );
  });

  test("mensagem não tem classe unread quando notificationIsRead é true", () => {
    const { container } = render(
      <NotificationRow
        notification={{ ...baseNotification, notificationIsRead: true }}
        onClick={jest.fn()}
        onSelectionChange={jest.fn()}
        isSelected={false}
      />
    );
    expect(container.querySelector(".notificationRow-message")).not.toHaveClass(
      "unread"
    );
  });
});
