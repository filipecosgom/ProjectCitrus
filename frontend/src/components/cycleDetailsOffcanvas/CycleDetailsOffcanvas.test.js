// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, opts) => {
      if (key === "cycles.cycleTitle" && opts?.id) return `Ciclo ${opts.id}`;
      if (key === "cycles.statusOpen") return "Aberto";
      if (key === "cycles.statusClosed") return "Fechado";
      if (key === "cycles.dateRange") return "Período";
      if (key === "cycles.startDate") return "Início";
      if (key === "cycles.endDate") return "Fim";
      if (key === "cycles.summary") return "Resumo";
      if (key === "cycles.duration") return "Duração";
      if (key === "cycles.daysDuration") return `${opts.days} dias`;
      if (key === "cycles.appraisals") return "Avaliações";
      if (key === "cycles.status") return "Estado";
      if (key === "cycles.appraisalsList") return "Lista de Avaliações";
      if (key === "cycles.statusCompleted") return "Completo";
      if (key === "cycles.statusInProgress") return "Em progresso";
      if (key === "cycles.statusPending") return "Pendente";
      if (key === "cycles.userId") return "Utilizador";
      if (key === "cycles.na") return "N/A";
      return key;
    },
  }),
}));
jest.mock("../../stores/useLocaleStore", () => () => "pt");

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import CycleDetailsOffcanvas from "./CycleDetailsOffcanvas";

describe("CycleDetailsOffcanvas", () => {
  const mockCycle = {
    id: 1,
    state: "OPEN",
    startDate: "2024-01-01",
    endDate: "2024-01-10",
    evaluations: [
      {
        state: "COMPLETED",
        appraisedUser: { name: "João", surname: "Silva" },
      },
      {
        state: "IN_PROGRESS",
        appraisedUser: { name: "Maria" },
      },
    ],
  };

  const mockOnClose = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("não renderiza nada se shouldRender for falso", () => {
    const { container } = render(
      <MemoryRouter>
        <CycleDetailsOffcanvas
          isOpen={false}
          onClose={mockOnClose}
          cycle={mockCycle}
        />
      </MemoryRouter>
    );
    expect(container.firstChild).toBeNull();
  });

  test("renderiza título, datas e avaliações quando aberto", () => {
    render(
      <MemoryRouter>
        <CycleDetailsOffcanvas
          isOpen={true}
          onClose={mockOnClose}
          cycle={mockCycle}
        />
      </MemoryRouter>
    );
    expect(screen.getByText("Ciclo 1")).toBeInTheDocument();
    // Corrigido: verifica se existem dois elementos com texto "Aberto"
    expect(screen.getAllByText("Aberto")).toHaveLength(2);
    expect(screen.getByText("Início:")).toBeInTheDocument();
    expect(screen.getByText("10/01/2024")).toBeInTheDocument();
    expect(screen.getByText("10 dias")).toBeInTheDocument();
    expect(screen.getByText("Avaliações:")).toBeInTheDocument();
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("Completo")).toBeInTheDocument();
    expect(screen.getByText("Maria")).toBeInTheDocument();
    expect(screen.getByText("Em progresso")).toBeInTheDocument();
  });

  test("chama onClose ao clicar no botão de fechar", () => {
    render(
      <MemoryRouter>
        <CycleDetailsOffcanvas
          isOpen={true}
          onClose={mockOnClose}
          cycle={mockCycle}
        />
      </MemoryRouter>
    );
    fireEvent.click(screen.getByRole("button"));
    expect(mockOnClose).toHaveBeenCalled();
  });

  test("chama onClose ao clicar no backdrop", () => {
    render(
      <MemoryRouter>
        <CycleDetailsOffcanvas
          isOpen={true}
          onClose={mockOnClose}
          cycle={mockCycle}
        />
      </MemoryRouter>
    );
    // Corrigido: o componente precisa do atributo data-testid no backdrop
    fireEvent.click(screen.getByTestId("cycle-details-offcanvas-backdrop"));
    expect(mockOnClose).toHaveBeenCalled();
  });
});
