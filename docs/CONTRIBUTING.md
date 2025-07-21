# Contribuir a RentSecure – Backend

¡Gracias por tu interés en contribuir!  
Sigue estos lineamientos para mantener un historial limpio y un flujo de trabajo predecible.

## 1. Convención de Commits

Usamos **Conventional Commits**. Prefijos más comunes:

| Tipo | Propósito |
|------|-----------|
| **feat:** | Nueva funcionalidad |
| **fix:** | Corrección de bug |
| **docs:** | Cambios solo de documentación |
| **test:** | Añadir o ajustar tests |
| **chore:** | Tareas internas (build, deps, refactor que no cambia API) |
| **ci:** | Cambios en pipelines CI/CD |

Ejemplo válido:

```
feat: add /health endpoint
```

## 2. Flujo de Pull Requests

1. Crea rama descriptiva desde `main`  
   ```
   git checkout -b feat/health-endpoint
   ```
2. Commits según la convención.  
3. Abre PR → verifica que el check **build** esté verde.  
4. Aprueba y **Squash and merge** (linear history).  
5. Elimina la rama.

## 3. Reglas de Branch Protection

- Push directo a `main` está **prohibido**.  
- Cada PR requiere el status check `build`.  
- Historial lineal (solo _Squash_ o _Rebase_).

¡Listo! Con esto mantenemos claridad y calidad en cada entrega.
