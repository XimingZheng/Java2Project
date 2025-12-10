import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  Cell,
} from 'recharts';
import type { MultithreadingResponse } from '../types/api';

interface MultithreadingChartProps {
  data: MultithreadingResponse;
}

// Category colors mapping
const CATEGORY_COLORS: { [key: string]: string } = {
  'synchronization': '#F48024',
  'deadlock': '#C73A63',
  'race-condition': '#FF6B6B',
  'thread-safety': '#0077CC',
  'performance': '#5EBA7D',
  'exception': '#9147FF',
  'memory': '#FFA500',
  'executor': '#4ECDC4',
  'default': '#8884d8',
};

// Custom tooltip component (outside render)
const CustomTooltip = ({ active, payload }: { active?: boolean; payload?: Array<{ payload: { name: string; count: number; category: string; color: string } }> }) => {
  if (active && payload && payload.length) {
    const data = payload[0].payload;
    return (
      <div className="bg-white p-4 rounded-lg shadow-lg border border-gray-200">
        <p className="font-semibold text-gray-800 mb-1">{data.name}</p>
        <p className="text-xs text-gray-500 mb-2">
          Category: <span className="font-medium">{data.category}</span>
        </p>
        <p className="text-sm text-gray-700">
          Occurrences: <span className="font-bold text-stackoverflow-orange">{data.count}</span>
        </p>
      </div>
    );
  }
  return null;
};

const MultithreadingChart: React.FC<MultithreadingChartProps> = ({ data }) => {
  // Transform data for chart
  const chartData = data.topProblems.map((problem) => ({
    name: problem.patternName,
    count: problem.count,
    category: problem.category,
    color: CATEGORY_COLORS[problem.category] || CATEGORY_COLORS['default'],
  }));

  // Get unique categories for legend
  const categories = Array.from(new Set(data.topProblems.map(p => p.category)));

  return (
    <div className="w-full">
      <div className="mb-4 p-4 bg-gray-50 rounded-lg">
        <p className="text-sm text-gray-600">
          Analyzed <span className="font-semibold">{data.totalThreads}</span> multithreading-related threads
        </p>
      </div>

      <ResponsiveContainer width="100%" height={450}>
        <BarChart
          data={chartData}
          margin={{ top: 20, right: 30, left: 20, bottom: 120 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
          <XAxis
            dataKey="name"
            angle={-45}
            textAnchor="end"
            height={140}
            tick={{ fontSize: 11 }}
            interval={0}
          />
          <YAxis
            label={{ value: 'Occurrence Count', angle: -90, position: 'insideLeft' }}
            tick={{ fontSize: 12 }}
          />
          <Tooltip content={<CustomTooltip />} />
          <Legend wrapperStyle={{ paddingTop: '20px' }} />
          <Bar dataKey="count" name="Occurrences" radius={[8, 8, 0, 0]}>
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.color} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>

      {/* Category legend */}
      <div className="mt-6 flex flex-wrap gap-3 justify-center">
        {categories.map((category) => (
          <div key={category} className="flex items-center gap-2">
            <div
              className="w-4 h-4 rounded"
              style={{ backgroundColor: CATEGORY_COLORS[category] || CATEGORY_COLORS['default'] }}
            />
            <span className="text-sm text-gray-700 capitalize">{category}</span>
          </div>
        ))}
      </div>

      <div className="mt-6 p-4 bg-yellow-50 rounded-lg border border-yellow-200">
        <h4 className="font-semibold text-yellow-900 mb-2">⚠️ Common Pitfalls</h4>
        <p className="text-sm text-yellow-800">
          These patterns represent the most frequently encountered problems in Java multithreading.
          Understanding these pitfalls is crucial for writing thread-safe and efficient concurrent code.
        </p>
      </div>

      {/* Top problem highlights */}
      {data.topProblems.length > 0 && (
        <div className="mt-4 grid grid-cols-1 md:grid-cols-3 gap-4">
          {data.topProblems.slice(0, 3).map((problem, index) => (
            <div
              key={problem.patternName}
              className="p-4 rounded-lg border-2"
              style={{ borderColor: CATEGORY_COLORS[problem.category] || CATEGORY_COLORS['default'] }}
            >
              <div className="flex items-center gap-2 mb-2">
                <span className="text-2xl font-bold text-gray-400">#{index + 1}</span>
                <span className="text-xs px-2 py-1 rounded bg-gray-100 text-gray-600">
                  {problem.category}
                </span>
              </div>
              <p className="text-sm font-semibold text-gray-800 mb-1">
                {problem.patternName}
              </p>
              <p className="text-xs text-gray-600">
                {problem.count} occurrences
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MultithreadingChart;
