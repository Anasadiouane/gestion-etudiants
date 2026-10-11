; Inno Setup 6.3+  (https://jrsoftware.org/isdl.php)
; Cree dist\Setup-GestionEtudiants-1.0.0.exe a partir de dist\GestionEtudiants (build-exe.bat)
#define AppName "Gestion Etudiants"
#define AppVersion "1.0.0"
#define AppExe "GestionEtudiants.exe"

[Setup]
; Ne jamais changer cet AppId: il permet de mettre a jour une version deja installee
AppId={{B3A1C6E2-5D1F-4C7A-9E43-2F6E7A1D0C11}
AppName={#AppName}
AppVersion={#AppVersion}
AppPublisher=Association El Kheir
; Installation pour l utilisateur courant: pas besoin de droits administrateur
PrivilegesRequired=lowest
PrivilegesRequiredOverridesAllowed=dialog
DefaultDirName={autopf}\GestionEtudiants
DisableProgramGroupPage=yes
OutputDir=dist
OutputBaseFilename=Setup-GestionEtudiants-{#AppVersion}
Compression=lzma2
SolidCompression=yes
WizardStyle=modern
UninstallDisplayIcon={app}\{#AppExe}
ArchitecturesInstallIn64BitMode=x64compatible
CloseApplications=yes
RestartApplications=no

[Languages]
Name: "french"; MessagesFile: "compiler:Languages\French.isl"
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"

[Files]
Source: "dist\GestionEtudiants\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{autoprograms}\{#AppName}"; Filename: "{app}\{#AppExe}"
Name: "{autodesktop}\{#AppName}"; Filename: "{app}\{#AppExe}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#AppExe}"; Description: "{cm:LaunchProgram,{#AppName}}"; Flags: nowait postinstall skipifsilent

; Les donnees (%LOCALAPPDATA%\GestionEtudiants\data.db) ne sont JAMAIS supprimees par la desinstallation.
