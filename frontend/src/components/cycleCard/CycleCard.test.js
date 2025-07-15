// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, opts) =>
      typeof opts === "object" && opts.id ? `Ciclo ${opts.id}` : key,
  }),
}));
jest.mock("../../stores/useLocaleStore", () => () => "pt");

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import CycleCard from "./CycleCard";

describe("CycleCard", () => {
  const mockCycleOpen = {
    id: 1,
    state: "OPEN",
    startDate: "2024-01-01",
    endDate: "2024-12-31",
    evaluations: [{}, {}],
  };

  const mockCycleClosed = {
    id: 2,
    state: "CLOSED",
    startDate: "2023-01-01",
    endDate: "2023-12-31",
    evaluations: [],
  };

  const onCloseCycle = jest.fn();
  const onCardClick = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza título, status e datas do ciclo OPEN", () => {
    render(
      <CycleCard
        cycle={mockCycleOpen}
        onCloseCycle={onCloseCycle}
        onCardClick={onCardClick}
      />
    );
    expect(screen.getByText("Ciclo 1")).toBeInTheDocument();
    expect(screen.getByText("cycles.statusOpen")).toBeInTheDocument();
    expect(screen.getByText("01/01/2024")).toBeInTheDocument();
    expect(screen.getByText("31/12/2024")).toBeInTheDocument();
    expect(screen.getByText("2")).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "cycles.closeCycle" })
    ).toBeInTheDocument();
  });

  test("renderiza título, status e datas do ciclo CLOSED", () => {
    render(
      <CycleCard
        cycle={mockCycleClosed}
        onCloseCycle={onCloseCycle}
        onCardClick={onCardClick}
      />
    );
    expect(screen.getByText("Ciclo 2")).toBeInTheDocument();
    expect(screen.getByText("cycles.statusClosed")).toBeInTheDocument();
    expect(screen.getByText("01/01/2023")).toBeInTheDocument();
    expect(screen.getByText("31/12/2023")).toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: "cycles.closeCycle" })
    ).not.toBeInTheDocument();
  });

  test("chama onCardClick ao clicar no card", () => {
    render(
      <CycleCard
        cycle={mockCycleOpen}
        onCloseCycle={onCloseCycle}
        onCardClick={onCardClick}
      />
    );
    fireEvent.click(screen.getByText("Ciclo 1"));
    expect(onCardClick).toHaveBeenCalledWith(mockCycleOpen);
  });

  test("chama onCloseCycle ao clicar no botão de fechar ciclo", () => {
    render(
      <CycleCard
        cycle={mockCycleOpen}
        onCloseCycle={onCloseCycle}
        onCardClick={onCardClick}
      />
    );
    fireEvent.click(screen.getByRole("button", { name: "cycles.closeCycle" }));
    expect(onCloseCycle).toHaveBeenCalledWith(mockCycleOpen.id);
  });
});
