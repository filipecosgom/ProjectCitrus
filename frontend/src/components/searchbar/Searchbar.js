import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { FiSearch, FiChevronDown } from 'react-icons/fi';
import styles from './Searchbar.module.css';

const SearchBar = ({ onSearch, offices}) => {
  const { register, handleSubmit, watch, setValue } = useForm({
    defaultValues: {
      query: '',
      searchType: 'email',
      accountState: '',
      office: '',
      resultsPerPage: 10
    }
  });

  // Carrega enums
    useEffect(() => {
      console.log(offices)
    }, []);

  // State for manual menu toggles (mobile-friendly)
  const [showSearchTypeMenu, setShowSearchTypeMenu] = useState(false);
  const [showOfficeMenu, setShowOfficeMenu] = useState(false);

  const onSubmit = (data) => {
    if (data.query.trim() || data.accountState || data.office) {
      onSearch(data);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className={styles.searchContainer}>
      {/* Search Input + Button */}
      <button type="submit" className={styles.searchButton}>
        <FiSearch />
      </button>
      
      <input
        {...register('query')}
        placeholder={`Search by ${watch('searchType').replace(/_/g, ' ')}...`}
        className={styles.searchInput}
      />

      {/* Search Type Dropdown */}
      <div className={styles.dropdown}>
        <button 
          type="button"
          className={styles.dropdownToggle}
          onClick={() => setShowSearchTypeMenu(!showSearchTypeMenu)}
          aria-expanded={showSearchTypeMenu}
        >
          {watch('searchType').replace(/_/g, ' ')} <FiChevronDown />
        </button>
        
        {showSearchTypeMenu && (
          <div className={styles.dropdownMenu}>
            <div 
              className={styles.menuItem}
              onClick={() => {
                setValue('searchType', 'email');
                setShowSearchTypeMenu(false);
              }}
              data-active={watch('searchType') === 'email'}
            >
              Email
            </div>
            
            <div 
              className={styles.menuItem}
              onClick={() => {
                setValue('searchType', 'name');
                setShowSearchTypeMenu(false);
              }}
              data-active={watch('searchType') === 'name'}
            >
              Name
            </div>
            
            <div className={`${styles.menuItem} ${styles.submenuTrigger}`}>
              <span>Role</span> <span>▶</span>
              <div 
              className={styles.menuItem}
              onClick={() => {
                setValue('searchType', 'role');
                setShowSearchTypeMenu(false);
              }}
              data-active={watch('searchType') === 'role'}
            >
              Role
            </div>
            </div>
          </div>
        )}
      </div>

      {/* Workplace Flyout Menu */}
      <div className={styles.dropdown}>
        <button 
          type="button"
          className={styles.dropdownToggle}
          onClick={() => setShowOfficeMenu(!showOfficeMenu)}
          aria-expanded={showOfficeMenu}
        >
          {watch('office') || 'office'} <FiChevronDown />
        </button>
        
        {showOfficeMenu && (
          <div className={styles.dropdownMenu}>
            <div 
              className={styles.menuItem}
              onClick={() => {
                setValue('office', '');
                setShowOfficeMenu(false);
              }}
              data-active={watch('office') === ''}
            >
              All offices
            </div>
            {offices.map(place => (
              <div
                key={place}
                className={styles.menuItem}
                onClick={() => {
                  setValue('office', place);
                  setShowOfficeMenu(false);
                }}
                data-active={watch('office') === place}
              >
                {place}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Account State Filter */}
      <select 
        {...register('accountState')} 
        className={styles.filterSelect}
      >
        <option value="">All States</option>
        <option value="COMPLETE">Complete</option>
        <option value="INCOMPLETE">Incomplete</option>
      </select>

      {/* Results Per Page - Hidden on mobile */}
      <div className={styles.resultsPerPage}>
        <select 
          {...register('resultsPerPage')} 
          className={styles.resultsSelect}
        >
          <option value={5}>5 per page</option>
          <option value={10}>10 per page</option>
          <option value={20}>20 per page</option>
        </select>
        <FiChevronDown className={styles.selectArrow} />
      </div>
    </form>
  );
};


export default SearchBar;