// Mocks primeiro!
jest.mock("../spinner/Spinner", () => () => <div>Spinner</div>);
jest.mock("../courseSearchBar/CourseSearchBar", () => (props) => (
  <div data-testid="searchbar" />
));
jest.mock("../courseRow/CourseRow", () => (props) => (
  <div data-testid="courserow">{props.course?.title}</div>
));
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, opts) => key,
  }),
}));
jest.mock("../../handles/handleNotification", () => jest.fn());
jest.mock("../../handles/handleAddCompletedCourseToUser", () => ({
  handleAddCompletedCourseToUser: jest.fn(),
}));

import React from "react";
import { render, screen } from "@testing-library/react";
import AddCompletedCourseOffcanvas from "./AddCompletedCourseOffcanvas";

describe("AddCompletedCourseOffcanvas", () => {
  const defaultProps = {
    isOpen: true,
    onClose: jest.fn(),
    onAdd: jest.fn(),
    availableCourses: [{ id: 1, title: "Curso Teste" }],
    userId: 1,
    userName: "João",
    userSurname: "Silva",
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza o título", () => {
    render(<AddCompletedCourseOffcanvas {...defaultProps} />);
    expect(
      screen.getByText("courses.addCompletedCourseTitle")
    ).toBeInTheDocument();
  });

  test("renderiza o botão de adicionar", () => {
    render(<AddCompletedCourseOffcanvas {...defaultProps} />);
    expect(screen.getByText("courses.addCompletedCourse")).toBeInTheDocument();
  });

  test("renderiza o botão de cancelar", () => {
    render(<AddCompletedCourseOffcanvas {...defaultProps} />);
    expect(screen.getByText("courses.cancel")).toBeInTheDocument();
  });

  test("renderiza a barra de pesquisa de cursos", () => {
    render(<AddCompletedCourseOffcanvas {...defaultProps} />);
    expect(screen.getByTestId("searchbar")).toBeInTheDocument();
  });
});
