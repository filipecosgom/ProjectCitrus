// Mocks primeiro!
jest.mock("recharts", () => ({
  PieChart: ({ children }) => <div data-testid="piechart">{children}</div>,
  Pie: ({ children }) => <div data-testid="pie">{children}</div>,
  Cell: (props) => <div data-testid="cell" {...props} />,
  Tooltip: () => <div data-testid="tooltip" />,
  Legend: () => <div data-testid="legend" />,
}));

import React from "react";
import { render, screen } from "@testing-library/react";
import AppraisalStatePieChart from "./AppraisalStatePieChart";

describe("AppraisalStatePieChart", () => {
  const t = (key) => key;

  test("renderiza o título traduzido", () => {
    render(
      <AppraisalStatePieChart inProgress={5} completed={10} closed={2} t={t} />
    );
    expect(
      screen.getByText("dashboard.appraisalsDistribution")
    ).toBeInTheDocument();
  });

  test("renderiza os elementos do gráfico", () => {
    render(
      <AppraisalStatePieChart inProgress={3} completed={7} closed={1} t={t} />
    );
    expect(screen.getByTestId("piechart")).toBeInTheDocument();
    expect(screen.getByTestId("pie")).toBeInTheDocument();
    expect(screen.getAllByTestId("cell")).toHaveLength(3);
    expect(screen.getByTestId("tooltip")).toBeInTheDocument();
    expect(screen.getByTestId("legend")).toBeInTheDocument();
  });
});
