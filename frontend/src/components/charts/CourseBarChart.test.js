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
import CourseBarChart from "./CourseBarChart";

describe("CourseBarChart", () => {
  const t = (key) => key;

  test("renderiza o título traduzido", () => {
    render(<CourseBarChart active={5} inactive={2} t={t} />);
    expect(screen.getByText("dashboard.courses")).toBeInTheDocument();
  });

  test("renderiza os elementos do gráfico", () => {
    render(<CourseBarChart active={3} inactive={7} t={t} />);
    expect(screen.getByTestId("container")).toBeInTheDocument();
    expect(screen.getByTestId("barchart")).toBeInTheDocument();
    expect(screen.getByTestId("bar")).toBeInTheDocument();
    expect(screen.getByTestId("xaxis")).toBeInTheDocument();
    expect(screen.getByTestId("yaxis")).toBeInTheDocument();
    expect(screen.getByTestId("tooltip")).toBeInTheDocument();
    expect(screen.getByTestId("legend")).toBeInTheDocument();
  });
});
