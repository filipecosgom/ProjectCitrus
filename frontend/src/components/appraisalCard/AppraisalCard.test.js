// Mocks primeiro!
jest.mock("../userIcon/UserIcon", () => () => <div data-testid="usericon" />);
jest.mock(
  "../appraisalScoreStarBadge/AppraisalScoreStarBadge",
  () => (props) => <div data-testid="scorebadge">{props.score}</div>
);
jest.mock("../appraisalStateBadge/AppraisalStateBadge", () => (props) => (
  <div data-testid="statebadge">{props.state}</div>
));
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));
jest.mock("../../utils/utilityFunctions", () => ({
  formatStringToDate: (date) => "15/07/2025",
}));

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import AppraisalCard from "./AppraisalCard";

const mockAppraisal = {
  id: 1,
  feedback: "Bom trabalho",
  cycleId: 2,
  score: 4,
  state: "completed",
  creationDate: [2025, 7, 1],
  endDate: [2025, 7, 15],
  appraisedUser: {
    name: "João",
    surname: "Silva",
    email: "joao@teste.com",
  },
  appraisingUser: {
    name: "Maria",
    surname: "Costa",
    email: "maria@teste.com",
  },
};

describe("AppraisalCard", () => {
  test("renderiza nome e email do avaliado", () => {
    render(<AppraisalCard appraisal={mockAppraisal} />);
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("joao@teste.com")).toBeInTheDocument();
  });

  test("renderiza nome e email do avaliador", () => {
    render(<AppraisalCard appraisal={mockAppraisal} />);
    expect(screen.getByText("Maria Costa")).toBeInTheDocument();
    expect(screen.getByText("maria@teste.com")).toBeInTheDocument();
  });

  test("renderiza score", () => {
    render(<AppraisalCard appraisal={mockAppraisal} />);
    expect(screen.getByTestId("scorebadge")).toHaveTextContent("4");
  });

  test("renderiza estado", () => {
    render(<AppraisalCard appraisal={mockAppraisal} />);
    expect(screen.getByTestId("statebadge")).toHaveTextContent("completed");
  });

  test("renderiza data formatada", () => {
    render(<AppraisalCard appraisal={mockAppraisal} />);
    expect(screen.getByText("15/07/2025")).toBeInTheDocument();
  });

  test("renderiza checkbox se showCheckbox=true", () => {
    render(<AppraisalCard appraisal={mockAppraisal} showCheckbox={true} />);
    expect(screen.getByRole("checkbox")).toBeInTheDocument();
  });

  test("chama onClick ao clicar no card", () => {
    const onClick = jest.fn();
    render(<AppraisalCard appraisal={mockAppraisal} onClick={onClick} />);
    fireEvent.click(screen.getByTestId("appraisal-card"));
    expect(onClick).toHaveBeenCalledTimes(1);
  });
});
