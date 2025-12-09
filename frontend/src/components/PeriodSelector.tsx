import React from 'react';

interface PeriodSelectorProps {
  period: string;
  onChange: (period: string) => void;
}

interface PeriodOption {
  value: string;
  label: string;
}

const PeriodSelector: React.FC<PeriodSelectorProps> = ({ period, onChange }) => {
  const periods: PeriodOption[] = [
    { value: 'day', label: 'Day' },
    { value: 'week', label: 'Week' },
    { value: 'month', label: 'Month' },
    { value: 'year', label: 'Year' },
  ];

  return (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-2">
        Period
      </label>
      <select
        value={period}
        onChange={(e) => onChange(e.target.value)}
        className="w-full border border-gray-300 rounded-lg px-4 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-stackoverflow-orange"
      >
        {periods.map((p) => (
          <option key={p.value} value={p.value}>
            {p.label}
          </option>
        ))}
      </select>
    </div>
  );
};

export default PeriodSelector;
