import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import SearchBar from "../../components/searchbar/Searchbar";
import TrainingCard from "../../components/trainingCard/TrainingCard";
import "./Training.css";

const Training = () => {
  const { t } = useTranslation();
  const [trainings, setTrainings] = useState([]);
  const [filteredTrainings, setFilteredTrainings] = useState([]);
  const [loading, setLoading] = useState(true);

  // CORREÇÃO: Dados mock com imagens do picsum.photos
  const mockTrainings = [
    {
      id: 1,
      title: "Bootstrap",
      category: "Frontend",
      language: "en",
      duration: 40,
      image: "https://picsum.photos/170/95?random=1",
    },
    {
      id: 2,
      title: "React, Vue & Angular",
      category: "Frontend",
      language: "pt",
      duration: 120,
      image: "https://picsum.photos/170/95?random=2",
    },
    {
      id: 3,
      title: "UI/UX Design Fundamentals",
      category: "UX/UI",
      language: "en",
      duration: 80,
      image: "https://picsum.photos/170/95?random=3",
    },
    {
      id: 4,
      title: "Node.js & Express",
      category: "Backend",
      language: "pt",
      duration: 60,
      image: "https://picsum.photos/170/95?random=4",
    },
    {
      id: 5,
      title: "Database Management",
      category: "Database",
      language: "en",
      duration: 100,
      image: "https://picsum.photos/170/95?random=5",
    },
    {
      id: 6,
      title: "Mobile Development",
      category: "Mobile",
      language: "pt",
      duration: 90,
      image: "https://picsum.photos/170/95?random=6",
    },
    {
      id: 7,
      title: "DevOps Essentials",
      category: "DevOps",
      language: "en",
      duration: 70,
      image: "https://picsum.photos/170/95?random=7",
    },
    {
      id: 8,
      title: "Advanced JavaScript",
      category: "Frontend",
      language: "pt",
      duration: 85,
      image: "https://picsum.photos/170/95?random=8",
    },
  ];

  // Simular carregamento dos dados
  useEffect(() => {
    const loadTrainings = async () => {
      setLoading(true);
      // Simular delay de API
      setTimeout(() => {
        setTrainings(mockTrainings);
        setFilteredTrainings(mockTrainings);
        setLoading(false);
      }, 1000);
    };

    loadTrainings();
  }, []);

  // FilterMenu espera array de strings (chaves), não objetos
  const filtersConfig = ["category", "language"];

  // FilterMenu espera arrays de valores, não objetos
  const filterOptions = {
    category: [
      "all",
      "Frontend",
      "Backend",
      "UX/UI",
      "Database",
      "Mobile",
      "DevOps",
    ],
    language: ["all", "pt", "en"],
  };

  const defaultValues = {
    query: "",
    searchType: "title",
    category: "all",
    language: "all",
    limit: 12,
  };

  // Função de pesquisa e filtros
  const handleSearch = (searchData) => {
    let filtered = trainings;

    // Filtrar por texto de pesquisa
    if (searchData.query && searchData.query.trim() !== "") {
      const query = searchData.query.toLowerCase();
      filtered = filtered.filter(
        (training) =>
          training.title.toLowerCase().includes(query) ||
          training.category.toLowerCase().includes(query)
      );
    }

    // Filtrar por categoria
    if (searchData.category && searchData.category !== "all") {
      filtered = filtered.filter(
        (training) => training.category === searchData.category
      );
    }

    // Filtrar por idioma
    if (searchData.language && searchData.language !== "all") {
      filtered = filtered.filter(
        (training) => training.language === searchData.language
      );
    }

    setFilteredTrainings(filtered);
  };

  return (
    <div className="training-page">
      <div className="training-header">
        <h1 className="training-title">{t("training.title")}</h1>

        <SearchBar
          onSearch={handleSearch}
          filtersConfig={filtersConfig}
          filterOptions={filterOptions}
          defaultValues={defaultValues}
          limitOptions={[8, 12, 16, 24]}
          placeholder={t("training.searchPlaceholder")}
        />
      </div>

      <div className="training-content">
        {loading ? (
          <div className="training-loading">
            <p>{t("training.loading")}</p>
          </div>
        ) : filteredTrainings.length === 0 ? (
          <div className="training-no-results">
            <p>{t("training.noResults")}</p>
          </div>
        ) : (
          <div className="training-grid">
            {filteredTrainings.map((training) => (
              <TrainingCard key={training.id} training={training} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Training;
