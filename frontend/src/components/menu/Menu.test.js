jest.mock("../../stores/useAuthStore", () => () => ({
  user: { id: 42 },
  isUserAdmin: () => true,
  isUserManager: () => false,
}));
jest.mock("../../handles/handleLogout", () => jest.fn(() => Promise.resolve()));
jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => key,
  }),
}));
jest.mock("../languages/LanguageDropdown", () => (props) => (
  <div data-testid="language-dropdown" {...props} />
));
jest.mock("../../assets/flags/flag-en.png", () => "flag-en.png");
jest.mock("../../assets/flags/flag-pt.png", () => "flag-pt.png");

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Menu from "./Menu";

describe("Menu", () => {
  const mockSetLanguage = jest.fn();
  const mockOnLogout = jest.fn();
  const mockOnClose = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza menu com itens principais", () => {
    render(
      <MemoryRouter>
        <Menu show={true} language="pt" setLanguage={mockSetLanguage} />
      </MemoryRouter>
    );
    expect(screen.getByText("menuDashboard")).toBeInTheDocument();
    expect(screen.getByText("menuProfile")).toBeInTheDocument();
    expect(screen.getByText("menuUsers")).toBeInTheDocument();
    expect(screen.getByText("menuTraining")).toBeInTheDocument();
    expect(screen.getByText("menuCycles")).toBeInTheDocument();
    expect(screen.getByText("menuSettings")).toBeInTheDocument();
    expect(screen.getByText("menuLogout")).toBeInTheDocument();
  });

  test("renderiza LanguageDropdown quando expandido", () => {
    render(
      <MemoryRouter>
        <Menu show={true} language="pt" setLanguage={mockSetLanguage} />
      </MemoryRouter>
    );
    expect(screen.getByTestId("language-dropdown")).toBeInTheDocument();
  });
    
    test("não renderiza LanguageDropdown quando não expandido", () => {
        render(
        <MemoryRouter>
            <Menu show={false} language="pt" setLanguage={mockSetLanguage} />
        </MemoryRouter>
        );
        expect(screen.queryByTestId("language-dropdown")).not.toBeInTheDocument();
    });
});
