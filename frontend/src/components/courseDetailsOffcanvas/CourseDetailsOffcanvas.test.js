// Mocks primeiro!
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => fallback || key,
  }),
}));
jest.mock("../../handles/handleGetCourseImage", () =>
  jest.fn(() => Promise.resolve({ success: false }))
);
jest.mock("../../handles/handleUpdateCourse", () => jest.fn());
jest.mock("../../stores/useAuthStore", () => () => ({
  isUserAdmin: () => true,
}));
jest.mock("../confirmationModal/ConfirmationModal", () => () => (
  <div data-testid="confirmation-modal" />
));
jest.mock("../courseEditOffCanvas/CourseEditOffCanvas", () => () => (
  <div data-testid="edit-offcanvas" />
));
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
import { render, screen } from "@testing-library/react";
import CourseDetailsOffcanvas from "./CourseDetailsOffcanvas";

describe("CourseDetailsOffcanvas", () => {
  const mockCourse = {
    id: "1",
    title: "Curso Teste",
    area: "Informática",
    language: "pt",
    duration: 2,
    creationDate: "2024-01-01",
    courseHasImage: false,
    courseIsActive: true,
    link: "/curso/1",
    description: "Descrição do curso",
  };

  const mockInactiveCourse = {
    ...mockCourse,
    courseIsActive: false,
  };

  const onClose = jest.fn();
  const onCourseStatusChange = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("não renderiza nada quando shouldRender é false", () => {
    const { container } = render(
      <CourseDetailsOffcanvas
        isOpen={false}
        onClose={onClose}
        course={mockCourse}
        onCourseStatusChange={onCourseStatusChange}
      />
    );
    expect(container.firstChild).toBeNull();
  });
});
