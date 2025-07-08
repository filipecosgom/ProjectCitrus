import "./Appraisals.css";
import SearchBar from "../../components/searchbar/Searchbar";
import React, { useState, useEffect, useRef } from "react";
import { useSearchParams } from "react-router-dom";
import Pagination from "../../components/pagination/Pagination";
import Spinner from "../../components/spinner/spinner";
import AppraisalCard from "../../components/appraisalCard/AppraisalCard";
import SortControls from "../../components/sortControls/SortControls";
import { useTranslation } from "react-i18next";
import { handleGetAppraisals } from "../../handles/handleGetAppraisals";
import { handleGetAppraisalStates } from "../../handles/handleGetEnums";
import {
  appraisalsSearchTypes,
  appraisalSearchFilters,
  appraisalsSortFields,
  buildAppraisalsSearchParams,
} from "../../utils/appraisalsSearchUtils";
import AppraisalOffCanvas from "../../components/appraisalOffCanvas/AppraisalOffCanvas";
import useAuthStore from "../../stores/useAuthStore";
import { handleGeneratePdfOfAppraisals } from "../../handles/handleGeneratePdfOfAppraisals";
import handleNotification from "../../handles/handleNotification";

export default function Appraisals() {
  const { t } = useTranslation();
  const [urlSearchParams, setUrlSearchParams] = useSearchParams();

  const [appraisals, setAppraisals] = useState([]);
  const [appraisalStates, setAppraisalStates] = useState([]);
  const user = useAuthStore((state) => state.user);
  const isAdmin = user?.userIsAdmin;
  const [resultsLoading, setResultsLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [pagination, setPagination] = useState({
    offset: 0,
    limit: 10,
    total: 0,
  });

  // Separate state for search/filter, sort, and pagination
  const [searchParams, setSearchParams] = useState({
    query: "",
    searchType: "appraisedUserName", // ✅ CORRIGIR: era "creationDate"
    limit: 10,
    state: "",
    score: "",
  });
  const [sort, setSort] = useState({
    sortBy: "endDate", // ✅ CORRIGIR: era "creationDate", usar "endDate" como padrão
    sortOrder: "DESCENDING",
  });
  const lastSearchRef = useRef(searchParams);

  // Estados para appraisal offcanvas
  const [selectedAppraisal, setSelectedAppraisal] = useState(null);
  const [offcanvasOpen, setOffcanvasOpen] = useState(false);

  // ✅ NOVO: Carregar parâmetros da URL ao inicializar
  useEffect(() => {
    const initializeFromURL = () => {
      const urlParams = Object.fromEntries(urlSearchParams.entries());

      // Extrair parâmetros de busca da URL
      const searchParamsFromURL = {
        query: urlParams.query || "",
        searchType: urlParams.searchType || "appraisedUserName", // ✅ CORRIGIR
        limit: parseInt(urlParams.limit) || 10,
        state: urlParams.state || "",
        score: urlParams.score || "",
      };

      // Extrair parâmetros de ordenação da URL
      const sortFromURL = {
        sortBy: urlParams.sortBy || "endDate", // ✅ CORRIGIR
        sortOrder: urlParams.sortOrder || "DESCENDING",
      };

      // Extrair parâmetros de paginação da URL
      const paginationFromURL = {
        offset: parseInt(urlParams.offset) || 0,
        limit: parseInt(urlParams.limit) || 10,
        total: 0, // será atualizado quando buscar dados
      };

      // Atualizar estados apenas se são diferentes dos atuais
      setSearchParams(searchParamsFromURL);
      setSort(sortFromURL);
      setPagination((prev) => ({ ...prev, ...paginationFromURL }));
    };

    initializeFromURL();
  }, []); // Executar apenas na primeira renderização

  // ✅ NOVO: Verificar se há um appraisal ID na URL ao carregar
  useEffect(() => {
    const appraisalId = urlSearchParams.get("appraisal");
    if (appraisalId && appraisals.length > 0) {
      const appraisal = appraisals.find((a) => a.id === parseInt(appraisalId));
      if (appraisal) {
        setSelectedAppraisal(appraisal);
        setOffcanvasOpen(true);
      }
    }
  }, [urlSearchParams, appraisals]);

  // ✅ NOVO: Atualizar URL quando parâmetros de busca, ordenação ou paginação mudarem
  useEffect(() => {
    const updateURL = () => {
      const newParams = new URLSearchParams();

      // Adicionar parâmetros de busca (apenas se não são valores padrão)
      if (searchParams.query) newParams.set("query", searchParams.query);
      if (searchParams.searchType !== "appraisedUserName")
        // ✅ CORRIGIR
        newParams.set("searchType", searchParams.searchType);
      if (searchParams.limit !== 10)
        newParams.set("limit", searchParams.limit.toString());
      if (searchParams.state) newParams.set("state", searchParams.state);
      if (searchParams.score) newParams.set("score", searchParams.score);

      // Adicionar parâmetros de ordenação (apenas se não são valores padrão)
      if (sort.sortBy !== "endDate") newParams.set("sortBy", sort.sortBy); // ✅ CORRIGIR
      if (sort.sortOrder !== "DESCENDING")
        newParams.set("sortOrder", sort.sortOrder);

      // Adicionar parâmetros de paginação (apenas se não são valores padrão)
      if (pagination.offset !== 0)
        newParams.set("offset", pagination.offset.toString());

      // Preservar appraisal ID se existir
      const currentAppraisalId = urlSearchParams.get("appraisal");
      if (currentAppraisalId) {
        newParams.set("appraisal", currentAppraisalId);
      }

      // Atualizar URL apenas se os parâmetros mudaram
      const newURLParams = newParams.toString();
      const currentURLParams = urlSearchParams.toString();

      if (newURLParams !== currentURLParams) {
        setUrlSearchParams(newParams, { replace: true });
      }
    };

    // Só atualizar URL após a inicialização
    if (!pageLoading) {
      updateURL();
    }
  }, [
    searchParams,
    sort,
    pagination.offset,
    pageLoading,
    urlSearchParams,
    setUrlSearchParams,
  ]);

  useEffect(() => {
    lastSearchRef.current = searchParams;
  }, [searchParams]);

  // Fetch appraisals
  async function fetchAppraisals(
    offset = pagination.offset,
    overrideParams = null
  ) {
    // Always ensure appraisingUserId is present for non-admins
    let baseParams = overrideParams || searchParams;
    if (!isAdmin) {
      baseParams = { ...baseParams, appraisingUserId: user?.id };
    }
    const params = buildAppraisalsSearchParams({
      ...baseParams,
      offset,
      sortBy: sort.sortBy,
      sortOrder: sort.sortOrder,
    });
    console.log("Params sent to backend:", params); // Debug log
    setResultsLoading(true);
    const result = await handleGetAppraisals(params);
    setAppraisals(result.appraisals || []);
    setPagination((prev) => ({
      ...prev,
      offset,
      limit: result.pagination?.limit || 10,
      total: result.pagination?.totalAppraisals || 0,
    }));
    setResultsLoading(false);
  }

  // Only one effect for fetching appraisals
  useEffect(() => {
    if (searchParams) {
      fetchAppraisals(pagination.offset, searchParams);
    }
    // eslint-disable-next-line
  }, [searchParams, pagination.offset, sort]);

  // On mount: fetch appraisal states and initial appraisals
  useEffect(() => {
    async function fetchInitial() {
      setPageLoading(true);
      const appraisalStates = await handleGetAppraisalStates();
      setAppraisalStates(appraisalStates);
      // If not admin, set initial search to only appraisals where user is manager
      setSearchParams((prev) =>
        isAdmin ? { ...prev } : { ...prev, appraisingUserId: user?.id }
      );
      setPageLoading(false);
    }
    fetchInitial();
    // eslint-disable-next-line
  }, []);

  // ✅ MODIFICAR: Handlers para atualizar tanto estado quanto URL
  const handleSearch = (query, searchType, limit, filters = {}) => {
    console.log("🔍 handleSearch received:", {
      query,
      searchType,
      limit,
      filters,
    }); // ✅ DEBUG

    const newSearchParams = {
      query,
      searchType,
      limit,
      ...filters,
    };

    console.log("🔍 newSearchParams:", newSearchParams); // ✅ DEBUG

    // Always ensure appraisingUserId is present for non-admins
    const finalParams = isAdmin
      ? newSearchParams
      : { ...newSearchParams, appraisingUserId: user?.id };

    console.log("🔍 finalParams:", finalParams); // ✅ DEBUG

    setSearchParams(finalParams);
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  // ✅ MODIFICAR: Adicionar parâmetro na URL ao abrir offcanvas (preservando outros parâmetros)
  const handleAppraisalClick = (appraisal) => {
    setSelectedAppraisal(appraisal);
    setOffcanvasOpen(true);

    // Preservar parâmetros existentes e adicionar appraisal ID
    const currentParams = new URLSearchParams(urlSearchParams);
    currentParams.set("appraisal", appraisal.id.toString());
    setUrlSearchParams(currentParams);
  };

  // ✅ MODIFICAR: Remover apenas o parâmetro appraisal da URL (preservando outros)
  const handleCloseOffcanvas = () => {
    setOffcanvasOpen(false);

    // Remover apenas o parâmetro appraisal, mantendo os outros
    const currentParams = new URLSearchParams(urlSearchParams);
    currentParams.delete("appraisal");
    setUrlSearchParams(currentParams);

    setTimeout(() => {
      setSelectedAppraisal(null);
    }, 300);
  };

  const handleSortChange = (newSort) => {
    setSort(newSort);
    setPagination((prev) => ({ ...prev, offset: 0 }));
  };

  const handlePageChange = (newOffset) => {
    setPagination((prev) => ({ ...prev, offset: newOffset }));
  };

  const handleAppraisalSave = (updatedAppraisal) => {
    setAppraisals((prev) =>
      prev.map((a) =>
        a.id === updatedAppraisal.id ? { ...a, ...updatedAppraisal } : a
      )
    );
    setSelectedAppraisal((prev) =>
      prev && prev.id === updatedAppraisal.id
        ? { ...prev, ...updatedAppraisal }
        : prev
    );
  };

  // PDF export handler
  const handleGetPdf = async () => {
    let params = buildAppraisalsSearchParams({
      ...searchParams,
      sortBy: sort.sortBy,
      sortOrder: sort.sortOrder,
    });
    // Remove pagination params
    delete params.limit;
    delete params.offset;
    // Always ensure appraisingUserId is present for non-admins
    if (!isAdmin) {
      params = { ...params, appraisingUserId: user?.id };
    }
    console.log("PDF export params:", params); // Debug log
    const result = await handleGeneratePdfOfAppraisals(params);
    if (result.success) {
      const url = window.URL.createObjectURL(
        new Blob([result.blob], { type: "application/pdf" })
      );
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "appraisals.pdf");
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } else {
      handleNotification("error", "appraisalsPdfExportError");
    }
  };

  if (pageLoading) return <Spinner />;

  const filtersConfig = appraisalSearchFilters(t, appraisalStates);

  return (
    <div className="appraisals-container">
      <div className="appraisals-searchBarAndButton">
        <SearchBar
          key={`searchbar-${searchParams.query}-${searchParams.searchType}-${searchParams.state}`} // ✅ FORÇAR re-render
          onSearch={handleSearch}
          searchTypes={appraisalsSearchTypes(t, isAdmin)}
          {...filtersConfig}
          onExportPdf={handleGetPdf}
          // ✅ NOVO: Valores iniciais baseados nos parâmetros da URL
          defaultValues={{
            query: searchParams.query,
            searchType: searchParams.searchType,
            limit: searchParams.limit,
            state: searchParams.state,
            score: searchParams.score,
          }}
        />
      </div>
      <SortControls
        fields={appraisalsSortFields(t)}
        sortBy={sort.sortBy}
        sortOrder={sort.sortOrder}
        onSortChange={handleSortChange}
      />

      {resultsLoading ? (
        <div className="appraisals-loading">
          <Spinner />
        </div>
      ) : appraisals.length === 0 ? (
        <div className="appraisals-empty">
          <p>{t("appraisalsNoResults")}</p>
        </div>
      ) : (
        <div>
          <div className="appraisals-grid">
            {appraisals.map((appraisal) => (
              <AppraisalCard
                key={appraisal.id}
                appraisal={appraisal}
                onClick={handleAppraisalClick}
              />
            ))}
          </div>
        </div>
      )}
      <AppraisalOffCanvas
        appraisal={selectedAppraisal}
        isOpen={offcanvasOpen}
        onClose={handleCloseOffcanvas} // ✅ USAR nova função
        onSave={handleAppraisalSave}
      />
      <Pagination
        offset={pagination.offset}
        limit={pagination.limit}
        total={pagination.total}
        onChange={handlePageChange}
      />
    </div>
  );
}
