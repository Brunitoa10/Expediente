% =====================================================================
%  Reglas de razonamiento investigativo - Expedientes: Casos Reales
%  Compatible con SWI-Prolog (podés exportar la base desde la consola
%  del juego y probarla con:  swipl reglas.pl).
%
%  Hechos que aportan los casos (sólo cuando el detective los descubre):
%    lugar_crimen(L).  hora_crimen(Desde, Hasta).
%    tiene_motivo(S, Desc).  tiene_medio(S, Desc).
%    declara_en(S, Lugar, Desde, Hasta).      % coartada declarada
%    ubicado_por(Evidencia, S, Lugar, Hora).  % evidencia que lo ubica
%    admite_en(S, Lugar, Hora).               % lo admite el propio sospechoso
%    desmiente(Testimonio, S, Lugar, Hora).   % un tercero desmiente la coartada
%    niega(S, Accion).  prueba_de(E, S, Accion).
%    descarta(E, S).                          % coartada verificada
%    vincula(E, S).                           % prueba científica directa
%    escrita_por(Documento, S).  estilo(Documento, Rasgo).
%    recolectada(E).  custodia_rota(E).  presentada(E).
% =====================================================================

% --- Triángulo clásico: motivo, medio y oportunidad -------------------
motivo(S, D) :- tiene_motivo(S, D).
medio(S, D)  :- tiene_medio(S, D).

estuvo_en(S, L, H) :- ubicado_por(_, S, L, H).
estuvo_en(S, L, H) :- admite_en(S, L, H).

oportunidad(S, L, H) :-
    lugar_crimen(L),
    hora_crimen(Desde, Hasta),
    estuvo_en(S, L, H),
    H >= Desde, H =< Hasta.

% --- Coartadas: toda coartada se verifica con fuentes independientes ---
contradiccion(S, E) :-
    declara_en(S, Declarado, Desde, Hasta),
    ubicado_por(E, S, Real, H),
    Declarado \= Real,
    H >= Desde, H =< Hasta.
contradiccion(S, E) :-
    declara_en(S, L, Desde, Hasta),
    desmiente(E, S, L, H),
    H >= Desde, H =< Hasta.
contradiccion(S, E) :-
    niega(S, Accion),
    prueba_de(E, S, Accion).

descartado(S, E) :- descarta(E, S).

% --- Vínculos: prueba directa o comparación de estilo (lingüística forense)
vinculo(E, S) :- vincula(E, S).
vinculo(E, S) :-
    escrita_por(Referencia, S),
    estilo(E, R1), E \= Referencia,
    estilo(Referencia, R1),
    estilo(E, R2), R1 \= R2,
    estilo(Referencia, R2).

% --- Conclusión del detective -----------------------------------------
sospechoso_principal(S) :-
    sospechoso(S),
    motivo(S, _),
    medio(S, _),
    oportunidad(S, _, _),
    \+ descartado(S, _).

confrontable(S, E) :- contradiccion(S, E).
confrontable(S, E) :- vinculo(E, S).

% --- Estándar del fiscal: prueba admisible y vinculante ----------------
prueba_admisible(E) :- recolectada(E), \+ custodia_rota(E).

prueba_valida_contra(S, E) :-
    presentada(E),
    prueba_admisible(E),
    vinculo(E, S).

acusacion_sostenible(S) :-
    sospechoso_principal(S),
    prueba_valida_contra(S, _).
