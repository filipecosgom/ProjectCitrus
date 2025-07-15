// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => fallback || key,
  }),
}));
jest.mock("../../handles/handleNotification", () => jest.fn());
jest.mock("../../handles/handleGetEnums", () => ({
  __esModule: true,
  handleGetCourseAreas: () => Promise.resolve(["INFORMATICA", "GESTAO"]),
}));
jest.mock("../../handles/handleCreateNewCourse", () => ({
  handleCreateNewCourse: jest.fn(() =>
    Promise.resolve({
      success: true,
      data: { data: { id: "123", title: "Novo Curso" } },
    })
  ),
  handleUploadCourseImage: jest.fn(() => Promise.resolve({ success: true })),
}));
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
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import CourseNewOffCanvas from "./CourseNewOffCanvas";

describe("CourseNewOffCanvas", () => {
  const onClose = jest.fn();
  const onSubmit = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("não renderiza nada quando shouldRender é false", () => {
    const { container } = render(
      <CourseNewOffCanvas
        isOpen={false}
        onClose={onClose}
        onSubmit={onSubmit}
      />
    );
    expect(container.firstChild).toBeNull();
  });

  test("renderiza campos principais quando aberto", async () => {
    render(
      <CourseNewOffCanvas isOpen={true} onClose={onClose} onSubmit={onSubmit} />
    );
    // Título
    expect(screen.getAllByRole("textbox")[0]).toBeInTheDocument();
    // Área (select)
    expect(screen.getByRole("combobox")).toBeInTheDocument();
    // Descrição
    expect(screen.getAllByRole("textbox")[1]).toBeInTheDocument();
    // Link
    expect(screen.getAllByRole("textbox")[2]).toBeInTheDocument();
  });

  test("chama onClose ao clicar no botão de fechar", () => {
    const { container } = render(
      <CourseNewOffCanvas isOpen={true} onClose={onClose} onSubmit={onSubmit} />
    );
    const closeIcon = container.querySelector(
      ".course-newCourse-offcanvas-close"
    );
    fireEvent.click(closeIcon);
    expect(onClose).toHaveBeenCalled();
  });
});
