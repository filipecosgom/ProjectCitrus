// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, opts) => {
      if (key === "cycles.createNewCycle") return "Criar novo ciclo";
      if (key === "cycles.startDate") return "Início";
      if (key === "cycles.endDate") return "Fim";
      if (key === "cycles.appraisalsToComplete")
        return `Avaliações: ${opts.count}`;
      if (key === "cycles.daysDuration") return `${opts.days} dias`;
      if (key === "cycles.createCycle") return "Criar ciclo";
      if (key === "cycles.creating") return "A criar...";
      if (key === "cycles.errorInvalidDateRange") return "Intervalo inválido";
      if (key === "cycles.cycleCreated") return "Ciclo criado!";
      if (key === "cycles.cycleCreatedEmailWarning") return "Aviso de email!";
      if (key === "cycles.errorCreateCycle") return "Erro ao criar ciclo";
      return key;
    },
  }),
}));
jest.mock("../../stores/useLocaleStore", () => () => "pt");
jest.mock("../../api/cyclesApi", () => ({
  createCycle: jest.fn(() => Promise.resolve({ success: true, data: {} })),
  fetchActiveUsersCount: jest.fn(() =>
    Promise.resolve({ success: true, data: { data: { totalUsers: 5 } } })
  ),
}));
jest.mock("react-calendar", () => () => <div data-testid="calendar-mock" />);

import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import CycleOffcanvas from "./CycleOffcanvas";

describe("CycleOffcanvas", () => {
  const mockOnClose = jest.fn();
  const mockOnCycleCreated = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("não renderiza nada se shouldRender for falso", () => {
    const { container } = render(
      <CycleOffcanvas
        isOpen={false}
        onClose={mockOnClose}
        onCycleCreated={mockOnCycleCreated}
      />
    );
    expect(container.firstChild).toBeNull();
  });

  test("renderiza título, datas e botão quando aberto", async () => {
    render(
      <CycleOffcanvas
        isOpen={true}
        onClose={mockOnClose}
        onCycleCreated={mockOnCycleCreated}
      />
    );
    expect(screen.getByText("Criar novo ciclo")).toBeInTheDocument();
    expect(screen.getByText("Início:")).toBeInTheDocument();
    expect(screen.getByText("Fim:")).toBeInTheDocument();
    expect(screen.getByText("Criar ciclo")).toBeInTheDocument();
    // Corrigido: procura por qualquer texto que comece por "Avaliações:"
    await waitFor(() => {
      expect(
        screen.getByText((content) => content.startsWith("Avaliações:"))
      ).toBeInTheDocument();
    });
  });

  test("chama onClose ao clicar no botão de fechar", () => {
    render(
      <CycleOffcanvas
        isOpen={true}
        onClose={mockOnClose}
        onCycleCreated={mockOnCycleCreated}
      />
    );
    const closeButton = screen.getByRole("button", { name: "" }); // O botão de fechar não tem texto
    fireEvent.click(closeButton);
    expect(mockOnClose).toHaveBeenCalled();
  });

  test("chama onClose ao clicar no backdrop", () => {
    render(
      <CycleOffcanvas
        isOpen={true}
        onClose={mockOnClose}
        onCycleCreated={mockOnCycleCreated}
      />
    );
    const backdrop = screen
      .getByText("Criar novo ciclo")
      .closest(".cycle-offcanvas-backdrop");
    fireEvent.click(backdrop);
    expect(mockOnClose).toHaveBeenCalled();
  });
});
