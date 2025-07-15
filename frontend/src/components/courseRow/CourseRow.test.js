// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => fallback || key,
  }),
}));
jest.mock("../../handles/handleGetCourseImage", () =>
  jest.fn(() => Promise.resolve({ success: false }))
);
jest.mock("../../assets/flags/flag-en.png", () => "flag-en.png");
jest.mock("../../assets/flags/flag-pt.png", () => "flag-pt.png");
jest.mock("../../assets/flags/flag-es.png", () => "flag-es.png");
jest.mock("../../assets/flags/flag-fr.png", () => "flag-fr.png");
jest.mock("../../assets/flags/flag-it.png", () => "flag-it.png");
jest.mock(
  "../../assets/templates/courseTemplate.png",
  () => "courseTemplate.png"
);

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import CourseRow from "./CourseRow";

describe("CourseRow", () => {
  const mockCourse = {
    id: "1",
    title: "Curso Teste",
    area: "Informática",
    language: "pt",
    duration: 2,
    courseHasImage: false,
    courseIsActive: true,
  };

  const mockInactiveCourse = {
    ...mockCourse,
    courseIsActive: false,
  };

  const onSelect = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza título, área, duração e bandeira", () => {
    render(<CourseRow course={mockCourse} onSelect={onSelect} />);
    expect(screen.getByText("Curso Teste")).toBeInTheDocument();
    expect(screen.getByText("Informática")).toBeInTheDocument();
    expect(screen.getByText("2h")).toBeInTheDocument();
    expect(screen.getByAltText("pt")).toHaveAttribute("src", "flag-pt.png");
  });

  test("renderiza imagem padrão se não houver imagem do curso", () => {
    render(<CourseRow course={mockCourse} onSelect={onSelect} />);
    expect(screen.getByAltText("Curso Teste")).toHaveAttribute(
      "src",
      "courseTemplate.png"
    );
  });

  test("chama onSelect ao clicar se o curso estiver ativo", () => {
    render(<CourseRow course={mockCourse} onSelect={onSelect} />);
    fireEvent.click(screen.getByText("Curso Teste"));
    expect(onSelect).toHaveBeenCalledWith(mockCourse);
  });

  test("não chama onSelect ao clicar se o curso estiver inativo", () => {
    render(<CourseRow course={mockInactiveCourse} onSelect={onSelect} />);
    fireEvent.click(screen.getByText("Curso Teste"));
    expect(onSelect).not.toHaveBeenCalled();
  });

  test("aplica classe 'inactive' se o curso estiver inativo", () => {
    const { container } = render(
      <CourseRow course={mockInactiveCourse} onSelect={onSelect} />
    );
    expect(
      container.querySelector(".course-search-row.inactive")
    ).toBeInTheDocument();
  });
});
