import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import SearchBar from "../../components/searchbar/Searchbar";
import TrainingCard from "../../components/trainingCard/TrainingCard";
import TrainingDetailsOffcanvas from "../../components/trainingDetailsOffcanvas/TrainingDetailsOffcanvas";
import "./Training.css";

const Training = () => {
  const { t } = useTranslation();
  const [trainings, setTrainings] = useState([]);
  const [filteredTrainings, setFilteredTrainings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedTraining, setSelectedTraining] = useState(null);
  const [offcanvasOpen, setOffcanvasOpen] = useState(false);

  // Dados mock com mais detalhes
  const mockTrainings = [
    {
      id: 1,
      title: "Bootstrap",
      category: "Frontend",
      language: "en",
      duration: 40,
      image: "https://picsum.photos/170/95?random=1",
      instructor: "João Silva",
      createdDate: "2024-01-15",
      description:
        "Learn Bootstrap 5 from scratch and build responsive websites quickly and efficiently. This comprehensive course covers all the essential components and utilities.",
    },
    {
      id: 2,
      title: "React, Vue & Angular",
      category: "Frontend",
      language: "pt",
      duration: 120,
      image: "https://picsum.photos/170/95?random=2",
      instructor: "Maria Santos",
      createdDate: "2024-02-10",
      description:
        "Domine os três principais frameworks JavaScript: React, Vue.js e Angular. Compare suas diferenças e aprenda quando usar cada um.",
    },
    {
      id: 3,
      title: "UI/UX Design Fundamentals",
      category: "UX/UI",
      language: "en",
      duration: 80,
      image: "https://picsum.photos/170/95?random=3",
      instructor: "Alice Johnson",
      createdDate: "2024-01-20",
      description:
        "Master the fundamentals of user interface and user experience design. Learn design principles, prototyping, and user research methods.",
    },
    {
      id: 4,
      title: "Node.js & Express",
      category: "Backend",
      language: "pt",
      duration: 60,
      image: "https://picsum.photos/170/95?random=4",
      instructor: "Carlos Oliveira",
      createdDate: "2024-02-05",
      description:
        "Construa aplicações web robustas com Node.js e Express. Aprenda sobre APIs RESTful, middleware e integração com bases de dados.",
    },
    {
      id: 5,
      title: "Database Management",
      category: "Database",
      language: "en",
      duration: 100,
      image: "https://picsum.photos/170/95?random=5",
      instructor: "Robert Brown",
      createdDate: "2024-01-25",
      description:
        "Learn database design, SQL queries, and database optimization. Cover both relational and NoSQL databases with practical examples.",
    },
    {
      id: 6,
      title: "Mobile Development",
      category: "Mobile",
      language: "pt",
      duration: 90,
      image: "https://picsum.photos/170/95?random=6",
      instructor: "Ana Costa",
      createdDate: "2024-02-15",
      description:
        "Desenvolva aplicações móveis nativas e híbridas. Aprenda React Native, Flutter e as melhores práticas para desenvolvimento mobile.",
    },
    {
      id: 7,
      title: "DevOps Essentials",
      category: "DevOps",
      language: "en",
      duration: 70,
      image: "https://picsum.photos/170/95?random=7",
      instructor: "David Wilson",
      createdDate: "2024-01-30",
      description:
        "Master DevOps practices including CI/CD, containerization with Docker, and infrastructure as code. Learn modern deployment strategies.",
    },
    {
      id: 8,
      title: "Advanced JavaScript",
      category: "Frontend",
      language: "pt",
      duration: 85,
      image: "https://picsum.photos/170/95?random=8",
      instructor: "Pedro Ferreira",
      createdDate: "2024-02-20",
      description:
        "Aprofunde seus conhecimentos em JavaScript com tópicos avançados como closures, protótipos, programação assíncrona e ES6+.",
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

  // NOVO: Função para abrir offcanvas
  const handleViewDetails = (training) => {
    setSelectedTraining(training);
    setOffcanvasOpen(true);
  };

  // NOVO: Função para fechar offcanvas
  const handleCloseOffcanvas = () => {
    setOffcanvasOpen(false);
    setSelectedTraining(null);
  };

  return (
    <div className="training-page">
      <div className="training-header">

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
              <TrainingCard
                key={training.id}
                training={training}
                onViewDetails={handleViewDetails}
              />
            ))}
          </div>
        )}
      </div>

      {/* NOVO: Offcanvas */}
      <TrainingDetailsOffcanvas
        isOpen={offcanvasOpen}
        onClose={handleCloseOffcanvas}
        training={selectedTraining}
      />
    </div>
  );
};

export default Training;
