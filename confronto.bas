Attribute VB_Name = "Modulo1"
Option Compare Database

Public Sub Tabelle_a_confronto(Tab1 As String, Tab2 As String)
    'Autore: Alberto Plano (albertoplano@iteco.it)
    'Input: Nome delle due tabelle/query da confrontare.
    'Tab1 e Tab2 devono avere la stessa struttura affinchè
    '            i risultati ottenuti siano sensati.
    'Tab1 e Tab2 non devono contenere campi di tipo OLE.
    Dim dbase As DAO.Database
    Dim T1 As DAO.Recordset, T2 As DAO.Recordset
    Dim campo As DAO.Field
    Dim aiuto1(), aiuto2() As String
    Dim indice1, indice2 As Long
    
    Set dbase = CurrentDb
    Set T1 = dbase.OpenRecordset(Tab1, dbOpenSnapshot)
    Set T2 = dbase.OpenRecordset(Tab2, dbOpenSnapshot)
    
    Rem Creo le matrici aiuto1 e aiuto2
    T1.MoveLast
    T2.MoveLast
    ReDim aiuto1(1 To T1.RecordCount)
    ReDim aiuto2(1 To T2.RecordCount)
    
    T1.MoveFirst
    For indice1 = 1 To T1.RecordCount
        aiuto1(indice1) = ""
        For indice2 = 0 To T1.Fields.Count - 1
            aiuto1(indice1) = aiuto1(indice1) + CStr(T1(indice2).Name) + " " + CStr(Null_to_zero(T1(indice2))) & Chr$(10)
        Next indice2
        T1.MoveNext
    Next indice1
    T2.MoveFirst
    For indice1 = 1 To T2.RecordCount
        aiuto2(indice1) = ""
        For indice2 = 0 To T2.Fields.Count - 1
            aiuto2(indice1) = aiuto2(indice1) + CStr(T1(indice2).Name) + " " + CStr(Null_to_zero(T2(indice2))) & Chr$(10)
        Next indice2
        T2.MoveNext
    Next indice1
    
    Rem Elimino le stringhe uguali che sono contenute in Aiuto1 e Aiuto2
    For indice1 = 1 To T1.RecordCount
        For indice2 = 1 To T2.RecordCount
            If aiuto1(indice1) = aiuto2(indice2) Then
                aiuto1(indice1) = ""
                aiuto2(indice2) = ""
                Exit For
            End If
        Next indice2
    Next indice1
    
    Rem Visualizzo i record di Tab1 che non sono contenuti in Tab2
    Dim messaggio As String
    Dim uguale As Integer
    uguale = True
    For indice1 = 1 To T1.RecordCount
        If aiuto1(indice1) <> "" Then
            messaggio = "Record presente in " & Tab1 & " e mancante in " & Tab2 & Chr$(10) & Chr$(10) & aiuto1(indice1)
            MsgBox messaggio
            uguale = False
        End If
    Next indice1
    For indice2 = 1 To T2.RecordCount
        If aiuto2(indice2) <> "" Then
            messaggio = "Record presente in " & Tab2 & " e mancante in " & Tab1 & Chr$(10) & Chr$(10) & aiuto2(indice2)
            MsgBox messaggio
            uguale = False
        End If
    Next indice2
    If uguale Then
        MsgBox Tab1 & " e " & Tab2 & " sono identiche."
    End If
End Sub

Public Function Null_to_zero(a As Variant) As Variant
    If IsNull(a) Then
        Null_to_zero = 0
    Else
        Null_to_zero = a
    End If
End Function
