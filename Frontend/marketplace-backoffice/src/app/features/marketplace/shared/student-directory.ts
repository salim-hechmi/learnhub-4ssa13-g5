/**
 * Affichage lisible des étudiants. Le Marketplace ne stocke QUE l'UUID Keycloak (référence logique) :
 * les noms ci-dessous correspondent aux données de démonstration. En production, remplacer par un appel
 * au profil Keycloak / user-profile (ou par l'e-mail X-User-Email).
 */
const DEMO_NAMES: Record<string, string> = {
  'a1b2c3d4-0000-4000-8000-000000000001': 'Yasmine Trabelsi',
  'a1b2c3d4-0000-4000-8000-000000000002': 'Karim Ben Salah',
  'a1b2c3d4-0000-4000-8000-000000000003': 'Ines Gharbi',
  'a1b2c3d4-0000-4000-8000-000000000004': 'Mehdi Jaziri',
  'a1b2c3d4-0000-4000-8000-000000000005': 'Salma Bouazizi'
};
const PALETTE = ['#2563eb', '#7c3aed', '#059669', '#db2777', '#ea580c', '#0891b2', '#4f46e5'];

export function studentName(id: string): string {
  return DEMO_NAMES[id] ?? 'Étudiant ' + id.slice(-4).toUpperCase();
}

export function studentInitials(id: string): string {
  return studentName(id).split(' ').filter(Boolean).slice(0, 2).map(p => p[0]).join('').toUpperCase();
}

export function studentColor(id: string): string {
  let h = 0;
  for (const c of id) h = (h * 31 + c.charCodeAt(0)) >>> 0;
  return PALETTE[h % PALETTE.length];
}
