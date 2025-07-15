jest.mock("../../stores/useLocaleStore", () => {
  let locale = "pt";
  return (selector) =>
    selector({
      locale,
      setLocale: (newLocale) => {
        locale = newLocale;
      },
    });
});
jest.mock("../../assets/flags/flag-en.png", () => "flag-en.png");
jest.mock("../../assets/flags/flag-pt.png", () => "flag-pt.png");

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import LanguageDropdown from "./LanguageDropdown";

describe("LanguageDropdown", () => {
  beforeEach(() => {
    jest.resetModules();
    // Recria o mock para garantir que o idioma começa em "pt"
    jest.doMock("../../stores/useLocaleStore", () => {
      let locale = "pt";
      return (selector) =>
        selector({
          locale,
          setLocale: (newLocale) => {
            locale = newLocale;
          },
        });
    });
  });

  test("renderiza idioma selecionado corretamente", () => {
    render(<LanguageDropdown />);
    expect(screen.getByText("Portuguese")).toBeInTheDocument();
    expect(screen.getByAltText("Portuguese")).toHaveAttribute(
      "src",
      "flag-pt.png"
    );
  });

  test("abre dropdown ao clicar no idioma", () => {
    render(<LanguageDropdown />);
    fireEvent.click(screen.getByText("Portuguese"));
    expect(screen.getByText("English")).toBeInTheDocument();
    expect(screen.getByAltText("English")).toHaveAttribute(
      "src",
      "flag-en.png"
    );
  });

  test("fecha dropdown ao clicar fora", () => {
    render(<LanguageDropdown />);
    fireEvent.click(screen.getByText("Portuguese"));
    expect(screen.getByText("English")).toBeInTheDocument();
    fireEvent.mouseDown(document.body);
    expect(screen.queryByText("English")).not.toBeInTheDocument();
  });

  test("altera idioma ao clicar numa opção", () => {
    render(<LanguageDropdown />);
    fireEvent.click(screen.getByText("Portuguese"));
    fireEvent.click(screen.getByText("English"));
    // Dropdown fecha e idioma muda
    expect(screen.getByText("English")).toBeInTheDocument();
    expect(screen.getByAltText("English")).toHaveAttribute(
      "src",
      "flag-en.png"
    );
    expect(screen.queryByText("Portuguese")).not.toBeInTheDocument();
  });

});
