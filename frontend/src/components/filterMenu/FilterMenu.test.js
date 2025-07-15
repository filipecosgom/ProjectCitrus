jest.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key) => {
      if (key === "filterMenuSettings") return "Definições";
      if (key === "filterMenuAllOffices") return "Todos os escritórios";
      if (key === "courses.sortArea") return "Área";
      if (key === "filterMenuOptionType1") return "Tipo 1";
      if (key === "filterMenuOptionType2") return "Tipo 2";
      if (key === "filterYes") return "Sim";
      if (key === "filterNo") return "Não";
      if (key === "filterAny") return "Qualquer";
      return key;
    },
  }),
}));

import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import FilterMenu from "./FilterMenu";

describe("FilterMenu", () => {
  const mockWatch = jest.fn();
  const mockSetValue = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renderiza botão de definições", () => {
    render(<FilterMenu watch={mockWatch} setValue={mockSetValue} />);
    expect(screen.getByText("Definições")).toBeInTheDocument();
  });

  test("abre e fecha o painel ao clicar no botão", () => {
    render(
      <FilterMenu
        watch={mockWatch}
        setValue={mockSetValue}
        filtersConfig={["area"]}
        filterOptions={{ area: ["Matemática"] }}
      />
    );
    const btn = screen.getByText("Definições");
    fireEvent.click(btn);
    expect(screen.getByText("Área")).toBeInTheDocument();
    fireEvent.click(btn);
    expect(screen.queryByText("Área")).not.toBeInTheDocument();
  });

  test("renderiza categorias e opções corretamente", () => {
    render(
      <FilterMenu
        watch={mockWatch}
        setValue={mockSetValue}
        filtersConfig={["area", "type"]}
        filterOptions={{
          area: ["Matemática", "Português"],
          type: ["type1", "type2"],
        }}
      />
    );
    fireEvent.click(screen.getByText("Definições"));
    expect(screen.getByText("Área")).toBeInTheDocument();
    expect(screen.getByText("filterMenuType")).toBeInTheDocument();
  });

  test("mostra opções ao passar o rato sobre categoria", () => {
    render(
      <FilterMenu
        watch={mockWatch}
        setValue={mockSetValue}
        filtersConfig={["type"]}
        filterOptions={{
          type: [
            { value: "type1", label: "Tipo 1" },
            { value: "type2", label: "Tipo 2" },
          ],
        }}
      />
    );
    fireEvent.click(screen.getByText("Definições"));
    const cat = screen.getByText("filterMenuType");
    fireEvent.mouseEnter(cat);
    expect(screen.getByText("Tipo 1")).toBeInTheDocument();
    expect(screen.getByText("Tipo 2")).toBeInTheDocument();
  });

  test("seleciona opção ao clicar e fecha o menu", () => {
    mockWatch.mockReturnValueOnce(null).mockReturnValueOnce("type1");
    render(
      <FilterMenu
        watch={mockWatch}
        setValue={mockSetValue}
        filtersConfig={["type"]}
        filterOptions={{
          type: [
            { value: "type1", label: "Tipo 1" },
            { value: "type2", label: "Tipo 2" },
          ],
        }}
      />
    );
    fireEvent.click(screen.getByText("Definições"));
    const cat = screen.getByText("filterMenuType");
    fireEvent.mouseEnter(cat);
    fireEvent.click(screen.getByText("Tipo 1"));
    expect(mockSetValue).toHaveBeenCalledWith("type", "type1");
    expect(screen.queryByText("Tipo 1")).not.toBeInTheDocument();
  });

  test("renderiza e alterna filtros tristate", () => {
    mockWatch.mockReturnValue(null);
    render(
      <FilterMenu
        watch={mockWatch}
        setValue={mockSetValue}
        tristateFilters={[
          { key: "ativo", label: "Ativo" },
          { key: "presente", label: "Presente" },
        ]}
      />
    );
    fireEvent.click(screen.getByText("Definições"));
    expect(screen.getByText("Ativo")).toBeInTheDocument();
    expect(screen.getByText("Presente")).toBeInTheDocument();

    const btn = screen.getAllByRole("button")[0];
    fireEvent.click(btn);
    expect(mockSetValue).toHaveBeenCalled();
  });

  test("mostra label correto para office", () => {
    render(
      <FilterMenu
        watch={mockWatch}
        setValue={mockSetValue}
        filtersConfig={["office"]}
        filterOptions={{
          office: [
            { value: null, label: "Todos os escritórios" },
            { value: "lisboa", label: "Lisboa" },
            { value: "porto", label: "Porto" },
          ],
        }}
      />
    );
    fireEvent.click(screen.getByText("Definições"));
    const cat = screen.getByText("filterMenuOffice");
    fireEvent.mouseEnter(cat);
    expect(screen.getByText("Todos os escritórios")).toBeInTheDocument();
    expect(screen.getByText("Lisboa")).toBeInTheDocument();
    expect(screen.getByText("Porto")).toBeInTheDocument();
  });
});
