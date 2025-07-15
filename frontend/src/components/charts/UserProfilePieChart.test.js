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
import UserProfilePieChart from "./UserProfilePieChart";

describe("UserProfilePieChart", () => {
  const t = (key) => key;

  test("renderiza o título traduzido", () => {
    render(<UserProfilePieChart complete={8} incomplete={2} t={t} />);
    expect(screen.getByText("dashboard.users")).toBeInTheDocument();
  });

  test("renderiza os elementos do gráfico", () => {
    render(<UserProfilePieChart complete={5} incomplete={5} t={t} />);
    expect(screen.getByTestId("piechart")).toBeInTheDocument();
    expect(screen.getByTestId("pie")).toBeInTheDocument();
    expect(screen.getAllByTestId("cell")).toHaveLength(2);
    expect(screen.getByTestId("tooltip")).toBeInTheDocument();
    expect(screen.getByTestId("legend")).toBeInTheDocument();
  });
});
