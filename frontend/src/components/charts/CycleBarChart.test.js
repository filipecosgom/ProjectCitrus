// Mocks primeiro!
jest.mock("recharts", () => ({
  BarChart: ({ children }) => <div data-testid="barchart">{children}</div>,
  Bar: () => <div data-testid="bar" />,
  XAxis: () => <div data-testid="xaxis" />,
  YAxis: () => <div data-testid="yaxis" />,
  Tooltip: () => <div data-testid="tooltip" />,
  Legend: () => <div data-testid="legend" />,
  ResponsiveContainer: ({ children }) => (
    <div data-testid="container">{children}</div>
  ),
}));

import React from "react";
import { render, screen } from "@testing-library/react";
import CycleBarChart from "./CycleBarChart";

describe("CycleBarChart", () => {
  const t = (key) => key;

  test("renderiza o título traduzido", () => {
    render(<CycleBarChart open={4} closed={2} t={t} />);
    expect(screen.getByText("dashboard.cycles")).toBeInTheDocument();
  });

  test("renderiza os elementos do gráfico", () => {
    render(<CycleBarChart open={1} closed={3} t={t} />);
    expect(screen.getByTestId("container")).toBeInTheDocument();
    expect(screen.getByTestId("barchart")).toBeInTheDocument();
    expect(screen.getByTestId("bar")).toBeInTheDocument();
    expect(screen.getByTestId("xaxis")).toBeInTheDocument();
    expect(screen.getByTestId("yaxis")).toBeInTheDocument();
    expect(screen.getByTestId("tooltip")).toBeInTheDocument();
  });
});
