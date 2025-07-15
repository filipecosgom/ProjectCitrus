// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));
jest.mock("../../handles/handleGetUserAvatar", () => ({
  handleGetUserAvatar: jest.fn(() => Promise.resolve({ success: false })),
}));
jest.mock("../userOffcanvas/UserOffcanvas", () => ({
  generateInitialsAvatar: () => "avatar-url",
}));
jest.mock("../appraisalStateBadge/AppraisalStateBadge", () => (props) => (
  <div data-testid="statebadge">{props.state}</div>
));
jest.mock(
  "../appraisalScoreStarBadge/AppraisalScoreStarBadge",
  () => (props) => <div data-testid="scorebadge">{props.score}</div>
);
jest.mock("../appraisalScoreVerbose/AppraisalScoreVerbose", () => (props) => (
  <div data-testid="scoreverbose">{props.score}</div>
));
jest.mock("../../handles/handleNotification", () => jest.fn());
jest.mock("../../handles/handleUpdateAppraisal", () => ({
  handleUpdateAppraisal: jest.fn(() => Promise.resolve({ success: true })),
}));
jest.mock("../../stores/useAuthStore", () => () => ({
  user: { id: 1 },
  isUserAdmin: () => true,
}));

import React from "react";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import AppraisalOffCanvas from "./AppraisalOffCanvas";

const mockAppraisal = {
  id: 1,
  feedback: "Muito bom trabalho",
  score: 4,
  state: "IN_PROGRESS",
  appraisedUser: {
    id: 2,
    name: "João",
    surname: "Silva",
    email: "joao@teste.com",
    role: "ADMIN_MANAGER",
    hasAvatar: false,
  },
  appraisingUser: {
    id: 1,
    name: "Maria",
    surname: "Costa",
    email: "maria@teste.com",
  },
  endDate: [2025, 7, 15],
};

describe("AppraisalOffCanvas", () => {
  const defaultProps = {
    appraisal: mockAppraisal,
    isOpen: true,
    onClose: jest.fn(),
    onSave: jest.fn(),
  };

  test("renderiza nome e email do avaliado", () => {
    render(
      <MemoryRouter>
        <AppraisalOffCanvas {...defaultProps} />
      </MemoryRouter>
    );
    expect(screen.getByText("João Silva")).toBeInTheDocument();
    expect(screen.getByText("joao@teste.com")).toBeInTheDocument();
  });

  test("renderiza o feedback", () => {
    render(
      <MemoryRouter>
        <AppraisalOffCanvas {...defaultProps} />
      </MemoryRouter>
    );
    expect(screen.getByText("Muito bom trabalho")).toBeInTheDocument();
  });

  test("renderiza o cargo formatado", () => {
    render(
      <MemoryRouter>
        <AppraisalOffCanvas {...defaultProps} />
      </MemoryRouter>
    );
    expect(screen.getByText(/admin manager/i)).toBeInTheDocument();
  });

  test("renderiza o score", () => {
    render(
      <MemoryRouter>
        <AppraisalOffCanvas {...defaultProps} />
      </MemoryRouter>
    );
    expect(screen.getByTestId("scorebadge")).toHaveTextContent("4");
    expect(screen.getByTestId("scoreverbose")).toHaveTextContent("4");
  });

  test("renderiza o estado", () => {
    render(
      <MemoryRouter>
        <AppraisalOffCanvas {...defaultProps} />
      </MemoryRouter>
    );
    expect(screen.getByTestId("statebadge")).toHaveTextContent("IN_PROGRESS");
  });
});
