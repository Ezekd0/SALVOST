interface Contribution {
  feature: string;
  contribution: number;
  value?: number;
}

export default function ShapExplanation({ json }: { json: string }) {
  let contributions: Contribution[] = [];
  try {
    const parsed: unknown = JSON.parse(json);
    if (Array.isArray(parsed)) {
      if (!parsed.every(item => item && typeof item.feature === 'string' && typeof item.contribution === 'number' && Number.isFinite(item.contribution))) throw new Error('Invalid contributions');
      contributions = parsed;
    } else if (parsed && typeof parsed === 'object') {
      contributions = Object.entries(parsed).map(([feature, contribution]) => {
        if (typeof contribution !== 'number' || !Number.isFinite(contribution)) throw new Error('Invalid contribution');
        return { feature, contribution };
      });
    } else throw new Error('Invalid explanation');
  } catch {
    return <p className="text-sm text-text-muted">SHAP explanation is unavailable for this result.</p>;
  }

  const maximum = Math.max(...contributions.map(item => Math.abs(item.contribution)), 0);
  return (
    <div className="space-y-3">
      <p className="text-xs text-text-muted">Feature contributions to this classification. Positive values increase the model output; negative values decrease it. Bar lengths are relative to the largest contribution.</p>
      {contributions.length === 0 && <p className="text-sm text-text-muted">No feature contributions were returned.</p>}
      {contributions.map((item, index) => (
        <div key={`${item.feature}-${index}`}>
          <div className="flex flex-wrap justify-between gap-2 text-xs mb-1">
            <span className="text-text-light">{item.feature.replace(/_/g, ' ')}</span>
            <span className={`font-mono ${item.contribution > 0 ? 'text-semantic-danger' : 'text-teal'}`}>
              {item.contribution > 0 ? '+' : ''}{item.contribution.toFixed(4)}
            </span>
          </div>
          {typeof item.value === 'number' && <p className="text-xs text-text-muted mb-1">Observed value: {item.value}</p>}
          <div className="w-full bg-navy-dark rounded-full h-2" aria-hidden="true">
            <div className={`h-2 rounded-full ${item.contribution > 0 ? 'bg-semantic-danger' : 'bg-teal'}`} style={{ width: `${maximum ? Math.abs(item.contribution) / maximum * 100 : 0}%` }} />
          </div>
        </div>
      ))}
    </div>
  );
}
