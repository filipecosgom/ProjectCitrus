// Mocks primeiro!
jest.mock("react-icons/fa6", () => ({
  FaStar: (props) => (
    <span data-testid="star" {...props}>
      ★
    </span>
  ),
}));

import React from "react";
import { render, screen } from "@testing-library/react";
import ScoreStarCard from "./AppraisalScoreStarBadge";

describe("ScoreStarCard", () => {
  test("renderiza mensagem quando score é null", () => {
    render(<ScoreStarCard score={null} />);
    expect(screen.getByText(/No score given/i)).toBeInTheDocument();
  });

  test("renderiza 4 estrelas por defeito", () => {
    render(<ScoreStarCard score={3} />);
    expect(screen.getAllByTestId("star")).toHaveLength(4);
  });

  test("renderiza o número correto de estrelas quando max é definido", () => {
    render(<ScoreStarCard score={2} max={5} />);
    expect(screen.getAllByTestId("star")).toHaveLength(5);
  });

  test("aplica a classe correta para score 4", () => {
    const { container } = render(<ScoreStarCard score={4} />);
    expect(container.firstChild).toHaveClass("stars-4");
  });

  test("aplica a classe correta para score 0", () => {
    const { container } = render(<ScoreStarCard score={0} />);
    expect(container.firstChild).toHaveClass("stars-0");
  });
});
