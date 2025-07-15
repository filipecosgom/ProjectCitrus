// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, options) => key,
  }),
}));
jest.mock("../../handles/handleGetCourses", () => ({
  handleGetCourses: jest.fn(() =>
    Promise.resolve({
      courses: [
        {
          id: "1",
          title: "Curso Teste",
          area: "Informática",
          language: "pt",
          duration: 2,
          courseHasImage: false,
          courseIsActive: true,
        },
        {
          id: "2",
          title: "Outro Curso",
          area: "Gestão",
          language: "en",
          duration: 3,
          courseHasImage: false,
          courseIsActive: true,
        },
      ],
    })
  ),
}));
jest.mock("../spinner/Spinner", () => () => <div>Spinner</div>);
jest.mock(
  "../../assets/templates/courseTemplate.png",
  () => "courseTemplate.png"
);
jest.mock("../courseRow/CourseRow", () => (props) => (
  <div
    data-testid="course-row"
    onClick={() => props.onSelect && props.onSelect(props.course)}
  >
    {props.course.title}
  </div>
));

import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import CourseSearchBar from "./CourseSearchBar";

describe("CourseSearchBar", () => {
  const onCourseSelect = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza input de pesquisa", () => {
    render(<CourseSearchBar onCourseSelect={onCourseSelect} />);
    expect(
      screen.getByPlaceholderText("courses.searchPlaceholder")
    ).toBeInTheDocument();
  });

  test("mostra mensagem de nenhum resultado", async () => {
    // Mock sem resultados
    require("../../handles/handleGetCourses").handleGetCourses.mockResolvedValueOnce(
      { courses: [] }
    );
    render(<CourseSearchBar onCourseSelect={onCourseSelect} />);
    fireEvent.change(screen.getByPlaceholderText("courses.searchPlaceholder"), {
      target: { value: "Nada" },
    });
    await waitFor(() => {
      expect(screen.getByText("courses.noResults")).toBeInTheDocument();
    });
  });

  test("limpa pesquisa ao clicar no botão de limpar", async () => {
    render(<CourseSearchBar onCourseSelect={onCourseSelect} />);
    fireEvent.change(screen.getByPlaceholderText("courses.searchPlaceholder"), {
      target: { value: "Curso" },
    });
    // Espera até aparecer o botão de limpar
    await waitFor(() => {
      expect(screen.getByTitle("courses.clear")).toBeInTheDocument();
    });
    fireEvent.click(screen.getByTitle("courses.clear"));
    expect(
      screen.getByPlaceholderText("courses.searchPlaceholder")
    ).toHaveValue("");
    // Não verifica course-row, pois a lista pode estar vazia após a limpeza
  });
});
