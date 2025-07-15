// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));

import React from "react";
import { render, screen } from "@testing-library/react";
import AppraisalScoreVerbose from "./AppraisalScoreVerbose";

describe("AppraisalScoreVerbose", () => {
  test("renderiza texto para score null", () => {
    render(<AppraisalScoreVerbose score={null} />);
    expect(screen.getByText("appraisal.scoreVerbose.null")).toBeInTheDocument();
  });

  test("renderiza texto para score 0", () => {
    render(<AppraisalScoreVerbose score={0} />);
    expect(screen.getByText("appraisal.scoreVerbose.null")).toBeInTheDocument();
  });

  test("renderiza texto para score 1", () => {
    render(<AppraisalScoreVerbose score={1} />);
    expect(screen.getByText("appraisal.scoreVerbose.1")).toBeInTheDocument();
  });

  test("renderiza texto para score 2", () => {
    render(<AppraisalScoreVerbose score={2} />);
    expect(screen.getByText("appraisal.scoreVerbose.2")).toBeInTheDocument();
  });

  test("renderiza texto para score 3", () => {
    render(<AppraisalScoreVerbose score={3} />);
    expect(screen.getByText("appraisal.scoreVerbose.3")).toBeInTheDocument();
  });

  test("renderiza texto para score 4", () => {
    render(<AppraisalScoreVerbose score={4} />);
    expect(screen.getByText("appraisal.scoreVerbose.4")).toBeInTheDocument();
  });
});
