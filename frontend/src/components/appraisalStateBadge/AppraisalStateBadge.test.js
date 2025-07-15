// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));
jest.mock("react-icons/ri", () => ({
  RiErrorWarningFill: () => <span data-testid="icon-inprogress" />,
}));
jest.mock("react-icons/fa6", () => ({
  FaCircleCheck: () => <span data-testid="icon-completed" />,
}));
jest.mock("react-icons/fa", () => ({
  FaLock: () => <span data-testid="icon-closed" />,
}));
jest.mock("react-icons/io", () => ({
  IoMdArrowDropdown: () => <span data-testid="icon-dropdown" />,
}));

import React from "react";
import { render, screen } from "@testing-library/react";
import AppraisalStateBadge from "./AppraisalStateBadge";

describe("AppraisalStateBadge", () => {
  test("renderiza estado IN_PROGRESS", () => {
    render(<AppraisalStateBadge state="IN_PROGRESS" />);
    expect(screen.getByText("appraisalStateInProgress")).toBeInTheDocument();
    expect(screen.getByTestId("icon-inprogress")).toBeInTheDocument();
  });

  test("renderiza estado COMPLETED", () => {
    render(<AppraisalStateBadge state="COMPLETED" />);
    expect(screen.getByText("appraisalStateCompleted")).toBeInTheDocument();
    expect(screen.getByTestId("icon-completed")).toBeInTheDocument();
  });

  test("renderiza estado CLOSED", () => {
    render(<AppraisalStateBadge state="CLOSED" />);
    expect(screen.getByText("appraisalStateClosed")).toBeInTheDocument();
    expect(screen.getByTestId("icon-closed")).toBeInTheDocument();
  });

  test("renderiza estado desconhecido", () => {
    render(<AppraisalStateBadge state="UNKNOWN" />);
    expect(screen.getByText("UNKNOWN")).toBeInTheDocument();
  });

  test("renderiza dropdown quando dropdownOption=true", () => {
    render(<AppraisalStateBadge state="COMPLETED" dropdownOption={true} />);
    expect(screen.getByTestId("icon-dropdown")).toBeInTheDocument();
  });
});
