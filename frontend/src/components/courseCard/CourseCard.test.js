// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));
jest.mock("../spinner/Spinner", () => () => <div data-testid="spinner" />);
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
import CourseCard from "./CourseCard";

describe("CourseCard", () => {
  const mockCourse = {
    id: "1",
    title: "Curso de Teste",
    area: "informática",
    language: "pt",
    courseHasImage: false,
    courseIsActive: true,
  };

  const mockInactiveCourse = {
    ...mockCourse,
    courseIsActive: false,
  };

  const onViewDetails = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza título, área e bandeira", () => {
    render(<CourseCard course={mockCourse} onViewDetails={onViewDetails} />);
    expect(screen.getByText("Curso de Teste")).toBeInTheDocument();
    expect(screen.getByText("Informática")).toBeInTheDocument();
    expect(screen.getByAltText("pt")).toHaveAttribute("src", "flag-pt.png");
  });

  test("renderiza botão de ver curso", () => {
    render(<CourseCard course={mockCourse} onViewDetails={onViewDetails} />);
    expect(screen.getByText("courses.viewCourse")).toBeInTheDocument();
  });

  test("chama onViewDetails ao clicar no botão", () => {
    render(<CourseCard course={mockCourse} onViewDetails={onViewDetails} />);
    fireEvent.click(screen.getByText("courses.viewCourse"));
    expect(onViewDetails).toHaveBeenCalledWith(mockCourse);
  });

  test("renderiza estado inativo", () => {
    render(
      <CourseCard course={mockInactiveCourse} onViewDetails={onViewDetails} />
    );
    expect(screen.getByText("(courses.inactive)")).toBeInTheDocument();
    // Filtra a imagem correta pela classe
    const imgs = screen.getAllByRole("img");
    const inactiveImg = imgs.find((img) =>
      img.className.includes("course-card-image-inactive")
    );
    expect(inactiveImg).toBeInTheDocument();
    expect(screen.getByText("Curso de Teste")).toBeInTheDocument();
  });
});
