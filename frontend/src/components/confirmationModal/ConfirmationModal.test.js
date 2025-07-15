// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => fallback || key,
  }),
}));
jest.mock("react-icons/io5", () => ({
  IoWarning: () => <span data-testid="icon-warning" />,
}));

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import ConfirmationModal from "./ConfirmationModal";

describe("ConfirmationModal", () => {
  const defaultProps = {
    isOpen: true,
    title: "confirmationModalTitle",
    message1: "confirmationModalMessage1",
    message2: "confirmationModalMessage2",
    onConfirm: jest.fn(),
    onClose: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("não renderiza nada quando isOpen é false", () => {
    const { container } = render(
      <ConfirmationModal {...defaultProps} isOpen={false} />
    );
    expect(container.firstChild).toBeNull();
  });

  test("renderiza título, mensagens e ícone de aviso", () => {
    render(<ConfirmationModal {...defaultProps} />);
    expect(screen.getByText("confirmationModalTitle")).toBeInTheDocument();
    expect(screen.getByText("confirmationModalMessage1")).toBeInTheDocument();
    expect(screen.getByText("confirmationModalMessage2")).toBeInTheDocument();
    expect(screen.getByTestId("icon-warning")).toBeInTheDocument();
  });

  test("chama onClose ao clicar no botão de fechar", () => {
    render(<ConfirmationModal {...defaultProps} />);
    fireEvent.click(screen.getByLabelText("Close"));
    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  test("chama onConfirm ao clicar no botão Confirm", () => {
    render(<ConfirmationModal {...defaultProps} />);
    fireEvent.click(screen.getByText("Confirm"));
    expect(defaultProps.onConfirm).toHaveBeenCalled();
  });

  test("chama onClose ao clicar no botão Cancel", () => {
    render(<ConfirmationModal {...defaultProps} />);
    fireEvent.click(screen.getByText("Cancel"));
    expect(defaultProps.onClose).toHaveBeenCalled();
  });
});
