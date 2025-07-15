// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, opts) => key,
  }),
}));
jest.mock("react-router-dom", () => ({
  useNavigate: () => jest.fn(),
}));
jest.mock("react-icons/io5", () => ({
  IoWarning: () => <span data-testid="icon-warning" />,
  IoClose: () => <span data-testid="icon-close" />,
}));

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import AppraisalWarningModal from "./AppraisalWarningModal";

describe("AppraisalWarningModal", () => {
  const defaultProps = {
    isOpen: true,
    onClose: jest.fn(),
    validationData: {
      totalAppraisals: 10,
      inProgressCount: 3,
      completedCount: 7,
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("não renderiza nada quando isOpen é false", () => {
    const { container } = render(
      <AppraisalWarningModal {...defaultProps} isOpen={false} />
    );
    expect(container.firstChild).toBeNull();
  });

  test("renderiza título e ícone de aviso", () => {
    render(<AppraisalWarningModal {...defaultProps} />);
    expect(screen.getByText("cycles.cannotCloseCycle")).toBeInTheDocument();
    expect(screen.getByTestId("icon-warning")).toBeInTheDocument();
  });

  test("renderiza estatísticas de avaliações", () => {
    render(<AppraisalWarningModal {...defaultProps} />);
    expect(
      screen.getByText((content, element) =>
        content.includes("cycles.totalAppraisals")
      )
    ).toBeInTheDocument();
    expect(screen.getByText("10")).toBeInTheDocument();
    expect(
      screen.getByText((content, element) =>
        content.includes("cycles.pendingAppraisals")
      )
    ).toBeInTheDocument();
    expect(screen.getByText("3")).toBeInTheDocument();
  });

  test("renderiza breakdown de avaliações", () => {
    render(<AppraisalWarningModal {...defaultProps} />);
    expect(screen.getByText("cycles.appraisalsBreakdown")).toBeInTheDocument();
    expect(screen.getByText("cycles.inProgressAppraisals")).toBeInTheDocument();
    expect(screen.getByText("cycles.completedAppraisals")).toBeInTheDocument();
  });

  test("chama onClose ao clicar no botão de fechar", () => {
    render(<AppraisalWarningModal {...defaultProps} />);
    const closeBtn = screen.getByLabelText("Close");
    fireEvent.click(closeBtn);
    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  test("chama onClose ao clicar no botão 'understood'", () => {
    render(<AppraisalWarningModal {...defaultProps} />);
    const understoodBtn = screen.getByText("cycles.understood");
    fireEvent.click(understoodBtn);
    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  test("renderiza botão para verificar avaliações pendentes", () => {
    render(<AppraisalWarningModal {...defaultProps} />);
    expect(
      screen.getByText("cycles.checkPendingAppraisals")
    ).toBeInTheDocument();
  });
});
